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
package com.automationrockstars.bmo.console.peer;

import com.google.common.base.Predicate;
import com.google.common.collect.Queues;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BusWaitingListener<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BusWaitingListener.class);
    @SuppressWarnings("rawtypes")
    private static final Predicate alwaysAccept = new Predicate() {
        @Override
        public boolean apply(Object input) {
            return true;
        }

    };
    private final EventBus source;
    BlockingQueue<T> responses = Queues.newLinkedBlockingQueue();

    public BusWaitingListener(final EventBus source) {
        this.source = source;
        this.source.register(this);
    }

    public static <T> BusWaitingListener<T> forType(Class<T> type, EventBus source) {
        return new BusWaitingListener<T>(source);
    }

    public void clean() {
        responses.clear();
    }

    public T get(long timeout, Predicate<T> check) {

        T response = null;
        boolean timedout = false;
        while (!timedout && response == null) {
            long start = System.currentTimeMillis();
            try {
                response = responses.poll(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOG.warn("Waiting for response interrupted");
            }

            if (response != null) {
                response = (check.apply(response)) ? response : null;
                timeout = timeout - (System.currentTimeMillis() - start);
                timedout = timeout <= 0;
                LOG.info("R: {} T: {}", response, timeout);
            } else {
                timedout = true;
            }

        }
        return response;

    }

    @SuppressWarnings("unchecked")
    public T get(long timeout) {
        return get(timeout, alwaysAccept);
    }

    @Subscribe
    public void handle(T response) {
        responses.add(response);
    }

    public void close() {
        source.unregister(this);
        responses.clear();
    }

}
