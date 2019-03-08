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
package com.automationrockstars.bmo.console.web;

import com.automationrockstars.bmo.console.peer.BusWaitingListener;
import com.automationrockstars.bmo.console.traffic.ConsoleEventBus;
import com.automationrockstars.bmo.console.traffic.HttpEventUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static org.apache.http.HttpHeaders.CONTENT_LENGTH;
import static org.apache.http.HttpHeaders.HOST;

public class SeleniumGridHandler implements HttpRequestHandler {


    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        HttpEventUtils.removeHeaders(request, CONTENT_LENGTH, HOST);
        BusWaitingListener<HttpResponse> listener = BusWaitingListener.forType(HttpResponse.class, ConsoleEventBus.execution);
        ConsoleEventBus.filter.post(request);
        EntityUtils.updateEntity(response, listener.get(0).getEntity());
    }

}
