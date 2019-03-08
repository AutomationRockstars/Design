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
import com.automationrockstars.bmo.event.processor.annotations.ExchangeReceiver;
import com.automationrockstars.bmo.event.processor.annotations.HttpReceiver;
import com.automationrockstars.bmo.event.processor.annotations.Rule;
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.EventListener;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class ProcessorFactory {

    static final String TOKEN = "::";
    private static final Logger LOG = LoggerFactory.getLogger(ProcessorFactory.class);
    private static final Map<String, MessageProcessor> processors = Maps.newTreeMap();

    public static synchronized void registerProcessors() {
        for (Method rule : RuleReporter.validRules()) {
            LOG.info("Registering rule {}", rule.getAnnotation(Rule.class).value());
            try {
                ProcessorFactory.registerHttpProcessors(rule, RuleReporter.getHttpReceivers(rule).toArray(HttpReceiver.class));
                ProcessorFactory.registerExchangeProcessors(rule, RuleReporter.getExchangeReceivers(rule).toArray(ExchangeReceiver.class));
            } catch (Throwable t) {
                LOG.error("Rule {} cannot be registered due to", rule, t);
            }
        }
        LOG.info("Registerd processors {}", Joiner.on("\n").join(processors().values()));
    }

    public static Map<String, MessageProcessor> processors() {
        return Collections.unmodifiableMap(processors);
    }

    public static void process(Message msg, String... keyParts) {
        MessageProcessor processor = processors.get(keyFromParts(keyParts));
        if (processor != null) {
            LOG.debug("Processing {} with processor {}", msg, processor);
            processor.process(msg);
        }
    }

    private static void registerHttpProcessors(Method method, HttpReceiver... receiver) {
        for (HttpReceiver rcv : receiver) {
            HttpProcessor processor = new HttpProcessor(method, rcv.type(), rcv.uri());
            processors.put(processor.key(), processor);
            LOG.info("HttpProcessor {} registered", processor);
        }
    }

    private static void registerExchangeProcessors(final Method method, ExchangeReceiver... receiver) {
        for (final ExchangeReceiver rcv : receiver) {
            ExchangeProcessor processor = new ExchangeProcessor(method, rcv.value(), rcv.key());
            processors.put(processor.key(), processor);
            RabbitEventBroker.consumer(rcv.value(), rcv.key())
                    .registerListener(new EventListener() {
                        @Override
                        public void onEvent(Event event) {
                            try {
                                process(Message.Builder.newMessage().withEvent(event), rcv.value(), rcv.key());
                            } catch (Throwable e) {
                                LOG.error("Error invoking rule {} with event {}", method, event);
                            }

                        }
                    });
            LOG.info("ExchangeProcessor {} registered", processor);
        }
    }

    private static String keyFromParts(String... parts) {
        StringBuilder key = new StringBuilder();
        for (String part : parts) {
            key.append(part).append(TOKEN);
        }
        return key.toString().replaceAll(TOKEN + "$", "");
    }

    static class HttpProcessor extends AbstractProcessor {

        public HttpProcessor(Method rule, String method, String uri) {
            super(rule, keyFromParts(method, uri));
        }

        public static final String methodFromKey(String key) {
            return key.split(TOKEN)[0];
        }

        public static final String uriFromKey(String key) {
            return key.split(TOKEN)[1];
        }

    }

    static class ExchangeProcessor extends AbstractProcessor {

        public ExchangeProcessor(Method method, String exchange, String routingKey) {
            super(method, keyFor(exchange, routingKey));
        }

        public static final String keyFor(String exchange, String routingKey) {
            return keyFromParts(exchange, routingKey);
        }

        public static final String exchangeFromKey(String key) {
            return key.split(TOKEN)[0];
        }

        public static final String rountingKeyFromKey(String key) {
            return key.split(TOKEN)[1];
        }

    }

}
