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
package com.automationrockstars.gunter.events;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public class EventStore {

    private static final ThreadLocal<Map<Class<? extends Event>, Event>> gevents = new InheritableThreadLocal<Map<Class<? extends Event>, Event>>() {
        @Override
        protected Map<Class<? extends Event>, Event> initialValue() {
            return Maps.newHashMap();
        }

        @Override
        protected Map<Class<? extends Event>, Event> childValue(Map<Class<? extends Event>, Event> parentValue) {
            return Maps.newHashMap(parentValue);

        }

    };

    public static final <T> T getEvent(Class<T> klass) {
        return (T) gevents.get().get(klass);
    }

    public static final void putEvent(Event event) {

        Class<? extends Event> key = (Class<? extends Event>) Iterables.find(Arrays.asList(event.getClass().getInterfaces()), new com.google.common.base.Predicate<Class<?>>() {
            @Override
            public boolean apply(Class<?> input) {
                return input.getName().startsWith("com.automationrockstars.gunter.events.");
            }

            ;
        });

        gevents.get().put(key, event);
    }


}
