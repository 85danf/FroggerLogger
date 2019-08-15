package com.jfrog.froggerlogger.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Runtime compiler on loan (with approval) from PlanckDB's illustrious maintainer, with some mild adaptation by yours truly.
 *
 * @author Gidi Shabat
 *///TODO [by dan]: probably not required
public class PlankcDBSimpleCompiler {
    private static final Logger log = LoggerFactory.getLogger(PlankcDBSimpleCompiler.class);

    private final String className;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final CharSequence source;

    public static byte[] compile(String className, CharSequence source) {
        return new PlankcDBSimpleCompiler(className, source).doCompile();
    }

    private PlankcDBSimpleCompiler(String className, CharSequence source) {
        this.className = className;
        this.source = source;
    }

    private byte[] doCompile() {
        String err = "Failed compiling agent class: " + className;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (CompilerFileManager manager = new CompilerFileManager(compiler, out)) {
            compileClass(err, compiler, manager);
            return out.toByteArray();
        } catch (Exception e) {
            log.error(err, e);
            throw new IllegalStateException(err, e);
        }
    }

    private List<CompilerFileObject> createFroggerLoggerCompilationUnits() throws URISyntaxException {
        return singletonList(new CompilerFileObject(className, JavaFileObject.Kind.SOURCE, source, out));
    }

    private void compileClass(String err, JavaCompiler compiler, CompilerFileManager manager) throws URISyntaxException {
        boolean success = compiler
                .getTask(null, manager, null, null, null, createFroggerLoggerCompilationUnits())
                .call();
        if (!success) {
            log.error(err);
            throw new IllegalStateException(err);
        }
    }
}
