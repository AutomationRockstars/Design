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

import com.automationrockstars.gunter.events.EventBroker;
import com.automationrockstars.gunter.events.EventBus;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import static com.automationrockstars.base.ConfigLoader.config;

public class RabbitEventBroker implements EventBroker {

    public static final String HOST_PROP = "rabbitmq.host";
    public static final String PORT_PROP = "rabbitmq.port";
    public static final String USER_PROP = "rabbitmq.user";
    public static final String PASS_PROP = "rabbitmq.pass";

    private static final ConnectionFactory factory = new ConnectionFactory();
    private static final Logger LOG = LoggerFactory.getLogger(RabbitEventBroker.class);
    private static final ConcurrentMap<String, Publisher> publishers = Maps.newConcurrentMap();
    private static final ConcurrentMap<String, Consumer> consumers = Maps.newConcurrentMap();
    private static final String DEFAULT_EXCHANGE = "testing-events";
    private static final String DEFAULT_KEY = "";
    private static Connection connection;
    private static Channel channel;
    private static RabbitEventBroker instance;
    private static Publisher defaultPublisher;
    private static Consumer defaultConsumer;

    private RabbitEventBroker() {
        EventBus.registerBroker(this);
    }

    private static RabbitEventBroker getInstance() {
        if (instance == null) {
            instance = new RabbitEventBroker();
        }
        return instance;
    }

    private static Channel reconnect() throws IOException, TimeoutException {
        getInstance().close();
        connection = factory.newConnection();
        channel = connection.createChannel();
        return channel;
    }

    static Channel getChannel() {
        try {
            if (channel == null || !connection.isOpen() || !channel.isOpen()) {
                Preconditions.checkNotNull(config().getString(HOST_PROP), "RabbitMQ host cannot be undefined");
                factory.setHost(config().getString(HOST_PROP));
                factory.setPort(config().getInt(PORT_PROP));
                factory.setUsername(config().getString(USER_PROP));
                factory.setPassword(config().getString(PASS_PROP));
                reconnect();
            }
        } catch (IOException | TimeoutException e) {
            LOG.error("Connection  {}:{}@{}:{} failed",
                    factory.getUsername(), factory.getPassword(), factory.getHost(), factory.getPort(), e);
        }
        Preconditions.checkState(connection.isOpen(), "Cannot establish connection to RabbitMQ server");
        return channel;
    }

    public static boolean declareExchange(String exchange) {
        try {
            getChannel().exchangeDeclare(exchange, "fanout", true);
            return true;
        } catch (IOException e) {
            LOG.error("Creating exchange {} failed", exchange, e);
            return false;
        }
    }

    public static Publisher publisher(String exchange, String routingKey) {
        Publisher result = publishers.get(exchange + "::" + routingKey);
        if (result == null) {
            result = new Publisher(exchange, routingKey);
            publishers.put(exchange + "::" + routingKey, result);
        }
        return result;
    }

    public static Consumer consumer(String exchange, String routingKey) {
        Consumer result = consumers.get(exchange + "::" + routingKey);
        if (result == null) {
            result = new Consumer(exchange, routingKey);
            consumers.put(exchange + "::" + routingKey, result);
        }
        return result;

    }

    public static final Publisher defaultPublisher() {
        if (defaultPublisher == null) {
            defaultPublisher = publisher(DEFAULT_EXCHANGE, DEFAULT_KEY);
        }
        return defaultPublisher;

    }

    public static final Consumer defaultConsumer() {
        if (defaultConsumer == null) {
            defaultConsumer = consumer(DEFAULT_EXCHANGE, DEFAULT_KEY);
        }
        return defaultConsumer;
    }

    public static void closeAll() {
        getInstance().close();
    }

    public static final void init() {
        getInstance();
        getChannel();
    }

    public void fireEvent(String event) {
        defaultPublisher().fireEvent(event);
    }

    public void close() {
        if (channel != null) {
            try {
                channel.close();
            } catch (Exception e) {
            }
        }
        if (connection != null) {
            try {
                connection.close();

            } catch (IOException e) {
            }
        }
        channel = null;
        connection = null;
    }

}
