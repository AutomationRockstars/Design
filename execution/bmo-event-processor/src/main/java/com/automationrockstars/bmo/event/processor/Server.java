/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

package com.automationrockstars.bmo.event.processor;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.event.processor.internal.CatchAllHttpListener;
import com.automationrockstars.bmo.event.processor.internal.ProcessorFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class Server {

    private static WebServer server;
    private static Logger LOG = LoggerFactory.getLogger(Server.class);

    private static void startServer() {
        try {
            server = WebServers.createWebServer(ConfigLoader.config().getInt("server.port", 8090))
                    .add(CatchAllHttpListener.get())
                    .start().get();
            LOG.info("HTTP server start on port {}", server.getPort());
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Cannot start server", e);
        }
    }

    private static void stopServer() {
        if (server != null) {
            try {
                server.stop().get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Problem stopping server", e);
            }
        }
    }

    public static void start() {
        startServer();
        ProcessorFactory.registerProcessors();
    }

    public static void stop() {
        stopServer();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (Paths.get("server.lock").toFile().exists()) {
            FileUtils.forceDelete(Paths.get("server.lock").toFile());
        }
        Server.start();
        File lockFile = Paths.get("server.lock").toFile();
        FileUtils.touch(lockFile);
        while (lockFile.exists() && lockFile.canRead()) {
            Thread.sleep(ConfigLoader.config().getInt("server.pull", 5000));
        }
        Server.stop();
    }
}
