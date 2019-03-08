package com.automationrockstars.monitoring.agent;


import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.monitoring.agent.monitor.CpuMonitor;
import com.automationrockstars.monitoring.agent.monitor.DynamicProcessMonitor;
import com.automationrockstars.monitoring.agent.monitor.MemMonitor;
import com.automationrockstars.monitoring.gunter.Emitter;
import com.automationrockstars.monitoring.gunter.Monitor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Server {

    public static final String PROCESS_FILTER = ConfigLoader.config().getString("monitor.process.filter", "java");
    public static final String NAME_FILTER = ConfigLoader.config().getString("monitor.process.name.regexp", "!(.*)");
    public static final boolean CPU = ConfigLoader.config().getBoolean("monitor.cpu", true);
    public static final boolean MEM = ConfigLoader.config().getBoolean("monitor.memory", true);
    private static final List<Monitor> monitorRegistry = new ArrayList<>();
    private static final ScheduledExecutorService runner = Executors.newScheduledThreadPool(10);
    private static final long SAMPLE_TIME = ConfigLoader.config().getLong("monitor.interval", 10);
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        server.start();
        LOG.info("STARTED");
        File lockFile = Paths.get("server.lock").toFile();
        FileUtils.touch(lockFile);
        FileUtils.waitFor(lockFile, 5);
        while (lockFile.exists() && lockFile.canRead()) {
            Thread.sleep(ConfigLoader.config().getInt("server.pull", 10000));
        }
        LOG.info("FINISHING");
        server.stop();
    }

    public void start() {
        monitorRegistry.add(
                new DynamicProcessMonitor(PROCESS_FILTER, NAME_FILTER));
        if (CPU) {
            monitorRegistry.add(new CpuMonitor());
        }
        if (MEM) {
            monitorRegistry.add(new MemMonitor());
        }

//		Configuration jmxMonitors = ConfigLoader.config().subset("monitor.jmx");

        runner.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Monitor m : monitorRegistry) {
                    Emitter.send(m.name(), m.sample());
                }
            }
        }, 1, SAMPLE_TIME, TimeUnit.SECONDS);
    }

    public void stop() {


        runner.shutdown();
        try {
            runner.awaitTermination(SAMPLE_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (Monitor monitor : monitorRegistry) {
            try {
                monitor.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Emitter.close();
    }


}
