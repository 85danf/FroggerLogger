package com.jfrog.froggerlogger.instrument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * Held statically by the agent to allow recurring calls to the injection mechanism.
 *
 * @author Dan Feldman
 */
public class LogInjectionHandler {
    private static final Logger log = LoggerFactory.getLogger(LogInjectionHandler.class);

    private Instrumentation instrumentation;

    public LogInjectionHandler(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public void inject() {
        LogInjectionTransformer transformer = createDynamicTransformer(null); //TODO [by dan]:
        try {
            //String[] args = userInput.split(SEPARATOR);
            instrumentation.addTransformer(transformer, true);
            //instrumentation.retransformClasses(Class.forName(args[0])); //TODO [by dan]:
        } catch (Exception e) {
            log.error("Failed registering Frogger Logger transformer: ", e);
        } finally {
            instrumentation.removeTransformer(transformer);
        }
    }


    /**
     * expected {@param args} order:
     *          args[0]: class name to inject log directive into
     *          args[1]: method name to inject log directive into
     *          args[2]: log directive to inject
     *          args[3]: line to inject at (absolute to class, not relative to method) //TODO [by dan]: is it?
     */
    private static LogInjectionTransformer createDynamicTransformer(String[] args) {
        return new LogInjectionTransformer(args[0], args[1], args[2], Integer.parseInt(args[3]));
    }

    public void shutdown() {
        //release the event bus and kill the polling thread

    }
}
