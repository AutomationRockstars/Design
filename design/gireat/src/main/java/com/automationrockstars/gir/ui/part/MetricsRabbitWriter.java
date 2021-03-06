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

package com.automationrockstars.gir.ui.part;

import com.automationrockstars.gunter.rabbit.RabbitEventBroker;

import java.util.Map;

import static com.automationrockstars.gunter.events.EventFactory.createSample;
import static com.automationrockstars.gunter.events.EventFactory.toJson;

public class MetricsRabbitWriter {

    public static void log(String host, String type, Map<String, Number> data) {
        if (!data.isEmpty()) {
            RabbitEventBroker.publisher("performance", "*").fireEvent(toJson(createSample("pci", type, data)));
        }
    }
}
