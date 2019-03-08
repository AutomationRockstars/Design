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

package com.automationrockstars.gir.data.impl;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gir.data.TestDataService;
import com.automationrockstars.gir.data.TestDataServices;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestDataServiceRegistry {

    private static final List<Class<? extends TestDataService>> classRegistry = Lists.newArrayList();
    private static final Logger LOG = LoggerFactory.getLogger(TestDataServiceRegistry.class);
    private static String[] packagesToScan = null;

    static {
        loadFromClasspath();
    }

    public static synchronized void register(Class<? extends TestDataService> serviceClass) {
        if (!classRegistry.contains(serviceClass)) {
            classRegistry.add(serviceClass);
        }
    }

    public static List<TestDataService> services() {
        List<TestDataService> result = Lists.newArrayList();
        for (Class<? extends TestDataService> serviceClass : classRegistry) {
            try {
                result.add(serviceClass.newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                LOG.error("Registering test data service {} failed", serviceClass, e);
            }
        }
        return result;
    }

    public static String[] packagesToScan() {
        if (packagesToScan == null) {
            String[] result = ConfigLoader.config().getStringArray("data.packages");

            if (result == null || result.length == 0) {
                result = stackTracePackages();
            }
            packagesToScan = result;
            LOG.info("Using {} to scan for data record classes", Arrays.toString(packagesToScan));

        }
        return packagesToScan;
    }

    public static String[] stackTracePackages() {
        String[] result = null;
        final AtomicInteger lastValidClass = new AtomicInteger(0);
        FluentIterable<String> classes = FluentIterable.from(Lists.reverse(Lists.newArrayList(Thread.currentThread().getStackTrace()))).transform(new Function<StackTraceElement, String>() {

            @Override
            public String apply(StackTraceElement input) {
                return input.getClassName();
            }
        });
        classes.firstMatch(new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                lastValidClass.incrementAndGet();
                return input.startsWith(TestDataServices.class.getName());
            }
        });
        result = classes.limit(lastValidClass.get()).transform(new Function<String, String>() {

            @Override
            public String apply(String input) {
                return String.format("%s.%s", input.split("\\.")[0], input.split("\\.")[1]);
            }
        }).filter(new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                return !input.startsWith("java.") && !input.startsWith("sun.");
            }
        }).toSet().toArray(new String[0]);

        return result;
    }

    private static void loadFromClasspath() {
        for (Class<? extends TestDataService> serviceClass : new Reflections((Object[]) stackTracePackages()).getSubTypesOf(TestDataService.class)) {
            register(serviceClass);
        }
        ;
    }
}
