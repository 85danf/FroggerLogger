package com.jfrog.froggerlogger.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Dan Feldman
 */
//TODO [by dan]: probably not required
public class CompilerFileObject extends SimpleJavaFileObject {

    private final CharSequence source;
    private final OutputStream out;

    CompilerFileObject(String className, Kind kind, CharSequence source, OutputStream out) throws URISyntaxException {
        super(new URI(null, null, resolveFileName(className, kind), null), kind);
        this.source = source;
        this.out = out;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return source == null ? super.getCharContent(ignoreEncodingErrors) : source;
    }

    @Override
    public OutputStream openOutputStream() {
        return out;
    }

    private static String resolveFileName(String className, Kind kind) {
        return className.replace('.', '/') + "." + (kind == Kind.CLASS ? "class" : "java");
    }
}
