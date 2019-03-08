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
package com.automationrockstars.bmo.console.traffic;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.console.peer.ConfigChangeListener;
import com.automationrockstars.bmo.console.peer.LogTraffic;
import com.automationrockstars.bmo.console.peer.RequestToNode;
import com.automationrockstars.bmo.console.peer.SessionHolder;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsoleEventBus {

    public static EventBus execution = new EventBus("execution");
    public static EventBus filter = new EventBus("filter");
    static ExecutorService logEvents = Executors.newCachedThreadPool();
    public static AsyncEventBus readOnly = new AsyncEventBus("log", logEvents);

    static {
        readOnly.register(new LogTraffic());
        filter.register(new SessionHolder());
//		filter.register(new Bypasser());
        execution.register(new RequestToNode());

        ConfigLoader.addEventListener(new ConfigChangeListener());

    }

    public static void shutdown() {
        SessionHolder.closeSessions();
        ConsoleEventBus.logEvents.shutdown();
        try {
            ConsoleEventBus.logEvents.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //nop
        }

    }
}
