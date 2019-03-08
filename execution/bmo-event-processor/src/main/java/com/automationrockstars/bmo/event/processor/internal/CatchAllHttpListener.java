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

package com.automationrockstars.bmo.event.processor.internal;

import com.automationrockstars.bmo.event.processor.Message;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class CatchAllHttpListener implements HttpHandler {

    private static CatchAllHttpListener INSTANCE;

    public static final CatchAllHttpListener get() {
        if (INSTANCE == null) {
            INSTANCE = new CatchAllHttpListener();
        }
        return INSTANCE;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        ProcessorFactory.process(Message.Builder.newMessage().withHttpRequest(request), request.method(), request.uri());
        response.end();
    }


}
