package com.jfrog.froggerlogger.compiler;

import javax.tools.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

/**
 * @author Dan Feldman
 */
//TODO [by dan]: probably not required
public  class CompilerFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final OutputStream out;

    CompilerFileManager(JavaCompiler compiler, OutputStream out) {
        super(compiler.getStandardFileManager(null, null, null));
        this.out = out;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        try {
            return new CompilerFileObject(className, kind, null, out);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
