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

import com.automationrockstars.bmo.event.processor.HttpEventUtils.RequestProducer;
import com.automationrockstars.bmo.event.processor.Message;
import com.automationrockstars.bmo.event.processor.annotations.Rule;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.rabbit.Publisher;
import com.google.common.base.Preconditions;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;

public class ResultSender {


    private static final Logger LOG = LoggerFactory.getLogger(ResultSender.class);

    public static synchronized void process(Object result, Method method) {
        if (result == null) return;
        Preconditions.checkArgument(Message.class.isAssignableFrom(result.getClass()), "Wrong type of object received %s", result.getClass());
        Message message = (Message) result;
        LOG.debug("Received result {} from rule {}", message, method.getAnnotation(Rule.class).value());
        if (message.toHttpContent() != null) {
            for (RequestProducer httpSender : RuleReporter.getHttpSenders(method)) {
                try {
                    HttpResponse resp = httpSender.execute(
                            message.toHttpContent()
                    ).returnResponse();
                    LOG.debug("Response from request {}", resp);
                } catch (IOException e) {
                    LOG.error("Cannot process message {} with sender {}", message.toHttpContent(), httpSender, e);
                }
            }
        }
        if (message.toEvent() != null) {
            for (Publisher eventSender : RuleReporter.getExchangeSenders(method)) {
                eventSender.fireEvent(EventFactory.toJson(message.toEvent()));
            }
        }
    }
}
