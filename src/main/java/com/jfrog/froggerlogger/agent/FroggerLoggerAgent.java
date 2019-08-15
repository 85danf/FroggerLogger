package com.jfrog.froggerlogger.agent;


import com.jfrog.froggerlogger.instrument.LogInjectionHandler;
import com.jfrog.froggerlogger.instrument.LogInjectionTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class FroggerLoggerAgent {
    private static final Logger log = LoggerFactory.getLogger(FroggerLoggerAgent.class);

    private static final String SEPARATOR = "@SEPARATOR@";
    private static LogInjectionHandler injector;

    private FroggerLoggerAgent() {

    }

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        injector = new LogInjectionHandler(instrumentation);
        Runtime.getRuntime().addShutdownHook(instrumentationShutdownTask());
    }

    private static Thread instrumentationShutdownTask() {
        return new Thread() {

            @Override
            public void run() {
                try {
                    if (injector != null) {
                        injector.shutdown();
                    }
                    injector = null;
                } catch (Exception e) {
                    log.error("Failed to shutdown Frogger Logger Instrumentation: ", e);
                }
            }
        };
    }

    //TODO [by dan]: agentmain is for dynamic attaching which is jdk-only, using premain to support jreE..
    /*public static void agentmain(String userInput, Instrumentation api) {
        try {
            String[] args = userInput.split(SEPARATOR);
            LogInjectionTransformer transformer = createDynamicTransformer(args);
            api.addTransformer(transformer, true);
            api.retransformClasses(Class.forName(args[0]));
            api.removeTransformer(transformer);
        } catch (Exception e) {
            log.error("Failed registering Frogger Logger transformer: ", e);
        }
    }*/


    //private static LogInjectionTransformer createDynamicTransformer(String[] args) {
    //    return new LogInjectionTransformer(args[0], args[1], args[2], Integer.parseInt(args[3]));
    //}

    /**
     * Utility to create an agent jar file that will be written into {@param out}.
     */
    public static void createAgentJar(OutputStream out, String agentClass, String bootClassPath,
            boolean canRedefineClasses, boolean canRetransformClasses, boolean canSetNativeMethodPrefix) throws IOException {
        final Manifest man = new Manifest();
        man.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        man.getMainAttributes().putValue("Agent-Class", agentClass);
        if (bootClassPath != null) {
            man.getMainAttributes().putValue("Boot-Class-Path", bootClassPath);
        }
        man.getMainAttributes().putValue("Can-Redefine-Classes", Boolean.toString(canRedefineClasses));
        man.getMainAttributes().putValue("Can-Retransform-Classes", Boolean.toString(canRetransformClasses));
        man.getMainAttributes().putValue("Can-Set-Native-Method-Prefix", Boolean.toString(canSetNativeMethodPrefix));
        try (JarOutputStream jarOut = new JarOutputStream(out, man)) {
            jarOut.flush();
        }
    }
}