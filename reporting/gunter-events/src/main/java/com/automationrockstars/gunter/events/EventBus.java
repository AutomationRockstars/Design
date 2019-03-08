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
package com.automationrockstars.gunter.events;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class EventBus {
    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    private static List<EventBroker> brokers = Lists.newArrayList();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                LOG.info("Closing EventBus");
                for (EventBroker broker : brokers)
                    try {
                        broker.close();
                    } catch (IOException e) {

                    }
            }
        }));
    }

    public static void registerBroker(EventBroker broker) {
        if (!brokers.contains(broker)) {
            brokers.add(broker);
        }
    }

    public static void removeBroker(EventBroker broker) {
        brokers.remove(broker);
    }

    public static void fireEvent(String event) {
        for (EventBroker broker : brokers) {
            try {
                broker.fireEvent(event);
                LOG.trace("Sent {} using {}", event, broker);
            } catch (Throwable e) {
                LOG.warn("Broker {} failed to send {} due to {}", broker, event, e);
            }
        }
    }
}
