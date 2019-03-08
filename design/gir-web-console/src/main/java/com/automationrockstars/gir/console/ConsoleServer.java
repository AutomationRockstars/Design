/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gir.console;

import com.automationrockstars.bmo.console.traffic.ConsoleEventBus;
import com.automationrockstars.bmo.console.web.*;
import com.google.common.net.InetAddresses;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

public class ConsoleServer {

    static HttpServer server;
    static HttpServer console;

    public static void startServer() throws Exception {
        if (server == null) {
            server = ServerBootstrap.bootstrap()
                    .setLocalAddress(InetAddresses.forUriString("0.0.0.0"))
                    .setListenerPort(8180)
                    .setServerInfo("WebConsoleDebug/1.1")
                    .registerHandler("*", new SeleniumGridHandler())
                    .create();
            server.start();

        }
        if (console == null) {
            console = ServerBootstrap.bootstrap()
                    .setListenerPort(8181)
                    .setLocalAddress(InetAddresses.forUriString("0.0.0.0"))
                    .setServerInfo("WebConsoleDisplay/1.1")
                    .registerHandler("/screenshot*", new ScreenshotHandler())
                    .registerHandler("/time", new TimeHandler())
                    .registerHandler("/config/*", new ConfigHandler())
                    .registerHandler("/*", new WebIdeHandler())

                    .create();
            console.start();

        }

    }

    public static void stopServer() throws Exception {
        server.stop();
        console.stop();
        ConsoleEventBus.shutdown();

    }

}
