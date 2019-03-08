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

package com.automationrockstars.gunter;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.EventFactory;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class ElasticSender {

    public static final String ES_URI_KEY = "ES_URI";

    public static void send(Event event) {
        send(EventFactory.toJson(event));
    }

    public static void send(String eventLoad) {
        try {
            System.out.println(Request.Post(ConfigLoader.config().getString(ES_URI_KEY)).bodyString(eventLoad, ContentType.APPLICATION_JSON).execute().returnContent().asString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
