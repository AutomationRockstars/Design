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

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.console.peer.BusWaitingListener;
import com.automationrockstars.bmo.console.traffic.ConsoleEventBus;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ConfigHandler implements HttpRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigHandler.class);
    private static final long MAX_HEAD_WAIT_TIME = 5000;

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        if ("POST".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            if (HttpEntityEnclosingRequest.class.isAssignableFrom(request.getClass())) {
                String content = EntityUtils.toString(((HttpEntityEnclosingRequest) request).getEntity());
                List<NameValuePair> configParams = URLEncodedUtils.parse(content, Charset.defaultCharset());
                for (NameValuePair param : configParams) {
                    ConfigLoader.config().setProperty(param.getName(), param.getValue());
                }
                LOG.info("Config changed");
            }
        } else if ("GET".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            String name = Iterables.getLast(Splitter.on("config/").split(request.getRequestLine().getUri()));
            try {
                response.setEntity(new StringEntity(ConfigLoader.config().getString(name)));
            } catch (Exception npe) {
                response.setReasonPhrase("No config parameter found");
                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            }
            LOG.info("Config fetched");
        } else if ("DELETE".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            String name = Iterables.getLast(Splitter.on("config/").split(request.getRequestLine().getUri()));
            ConfigLoader.config().clearProperty(name);

        } else if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
            final String name = Iterables.getLast(Splitter.on("config/").split(request.getRequestLine().getUri()));
            BusWaitingListener<ConfigurationEvent> listener = BusWaitingListener.forType(ConfigurationEvent.class, ConsoleEventBus.execution);
            try {
                LOG.info("Listener for property {} created", name);
                listener.clean();
                ConfigurationEvent event = null;
                event = listener.get(MAX_HEAD_WAIT_TIME, new Predicate<ConfigurationEvent>() {
                    @Override
                    public boolean apply(ConfigurationEvent input) {
                        return !input.isBeforeUpdate() &&
                                input.getPropertyName().equalsIgnoreCase(name);
                    }
                });
                Preconditions.checkNotNull(event, "Timeout while waiting for update");
                LOG.info("Listener finished with result {}", event);
            } catch (Exception e) {
                LOG.error("Configuration checking problem: {}", e.getMessage());
                LOG.trace("Details", e);
                response.addHeader("X-TIMEOUT", "true");
            }
            listener.close();
            listener = null;
            response.setStatusCode(HttpStatus.SC_NO_CONTENT);
            LOG.info("Config listener for property {} finished", name);
        } else {
            response.setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
        }
        LOG.info("Response sent");
    }

}
