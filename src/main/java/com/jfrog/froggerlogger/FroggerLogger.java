package com.jfrog.froggerlogger;

import com.jfrog.froggerlogger.agent.FroggerLoggerAgent;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class FroggerLogger {

    public void injectLog(String className, String methodName, String logToInject, int lineToInjectInto) throws Exception {
        try {

        } catch (Exception e) {
        }
    }

    public void loadAgent() throws IOException {
        String pid = getVmPid();
        File temporaryAgentJar = createTemporaryAgentJar();
        //TODO [by dan]: probably not possible on non-oracle non-jdk vms - other solution is to pass agent as -D param
        //TODO [by dan]: and init with premain instead of agentmain
        ByteBuddyAgent.attach(temporaryAgentJar, pid);
    }

    private String getVmPid() {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        return nameOfRunningVM.substring(0, p);
    }

    private File createTemporaryAgentJar() throws IOException {
        String agentClass = FroggerLoggerAgent.class.getName();
        File jarFile = File.createTempFile("javaagent." + agentClass, ".jar");
        jarFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(jarFile)) {
            FroggerLoggerAgent.createAgentJar(out, agentClass, null, true, true, false);
        }
        return jarFile;
    }
}
