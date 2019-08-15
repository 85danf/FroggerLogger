package com.jfrog.froggerlogger.instrument;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class LogInjectionTransformer implements ClassFileTransformer {
    private static final Logger log = LoggerFactory.getLogger(LogInjectionTransformer.class);

    private final String classToModify;
    private final String methodName;
    private final String logToInject;
    private final int lineToInjectAt;

    LogInjectionTransformer(String classToModify, String methodName, String logToInject, int lineToInjectAt) {
        this.classToModify = classToModify;
        this.methodName = methodName;
        this.logToInject = logToInject;
        this.lineToInjectAt = lineToInjectAt;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        byte[] bytesToReturn = classfileBuffer;
        if (shouldActOnClass(className)) {
            try {
                CtClass ctClass = injectLogDirective(classfileBuffer);
                bytesToReturn = ctClass.toBytecode();
                ctClass.detach();
            } catch (Exception ex) {
                log.error("Error occurred while attempting to inject log directive '{}' into class '{}' method '{}' at line '{}}'",
                        logToInject, className, methodName, lineToInjectAt);
            }
        }
        return bytesToReturn;
    }

    private CtClass injectLogDirective(byte[] classfileBuffer) throws IOException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
        //Locate required method, if found do the injection
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (shouldActOnMethod(method)) {
                log.debug("Injecting log directive '{}' into class '{}' method '{}' at line '{}'", logToInject,
                        ctClass.getName(), method.getName(), lineToInjectAt);
                method.insertAt(lineToInjectAt, logToInject);
            }
        }
        return ctClass;
    }

    private boolean shouldActOnMethod(CtMethod method) {
        return method.getName().equals(methodName);
    }

    private boolean shouldActOnClass(String className) {
        return className.replace("/", ".").equals(classToModify);
    }

    /*private byte[] recompile(String className) {
        String source = "package com.jfrog.bytecode;\n" +
                "\n" +
                "public class Example {\n" +
                "    public void doJob() {\n" +
                "        System.out.println(\"default code extended\");\n" +
                "    }\n" +
                "}\n";
        return PlankcDBSimpleCompiler.compile(className, source);
    }*/
}
