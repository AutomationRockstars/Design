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
package com.automationrockstars.gunter.rabbit;

import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Publisher {

    private static final Logger LOG = LoggerFactory.getLogger(Publisher.class);
    private final String exchange;
    private final String routingKey;

    protected Publisher(String exchange, String routingKey) {
        RabbitEventBroker.declareExchange(exchange);
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void fireEvent(String event) {
        try {
            RabbitEventBroker.getChannel().basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, event.getBytes());
        } catch (IOException e) {
            LOG.error("Cannot publish event {} on {}", event, exchange, e);
        }
    }

    @Override
    public String toString() {
        return String.format("Publisher to {},{} using {}", exchange, routingKey, RabbitEventBroker.getChannel());

    }
}
