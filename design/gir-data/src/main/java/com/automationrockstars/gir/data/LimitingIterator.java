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

package com.automationrockstars.gir.data;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LimitingIterator<E> implements Iterator<E> {

    private static final Logger LOG = LoggerFactory.getLogger(LimitingIterator.class);
    private final List<E> data;
    private final List<RateLimiter> counters = Lists.newArrayList();
    private final ReentrantReadWriteLock.WriteLock access = new ReentrantReadWriteLock().writeLock();
    public boolean cycle = true;
    private int pointer = 0;

    public LimitingIterator(Collection<E> data, double ratePerSecond) {
        this.data = Lists.newArrayList(data);
        for (int i = 0; i < data.size(); i++) {
            counters.add(RateLimiter.create(ratePerSecond));
        }
    }

    @Override
    public boolean hasNext() {
        return cycle || pointer < data.size();
    }

    @Override
    public E next() {
        try {
            access.lock();
            if (pointer == data.size()) {
                if (cycle) {
                    pointer = 0;
                } else {
                    return null;
                }
            }
            E result = data.get(pointer++);
            LOG.warn("Waiting for {}", result);
            double waited = counters.get(pointer - 1).acquire();
            LOG.warn("Waited {} for {}", waited, result);
            return result;
        } finally {
            access.unlock();
        }
    }

    @Override
    public void remove() {
        if (pointer == data.size()) {
            if (cycle) {
                pointer = 1;
            }
        } else pointer++;
    }

}
