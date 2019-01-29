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

import com.automationrockstars.gir.data.impl.SimpleTestDataPool;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

public class TestDataServices {


    private static final ConcurrentMap<String, TestDataPool> POOLS = Maps.newConcurrentMap();
    private static final String GLOBAL_POOL = "GLOBAL";


    public static synchronized TestDataPool pool(String poolName) {
        TestDataPool pool = POOLS.get(poolName);
        if (pool == null) {
            pool = new SimpleTestDataPool(poolName);
            POOLS.put(poolName, pool);
        }
        return pool;
    }

    public static TestDataPool pool() {
        return pool(GLOBAL_POOL);
    }

    public static synchronized void loadFrom(String jsonFile) {
        pool().loadFrom(jsonFile);
    }


    public static void close() {
        pool().close();
    }


    public static <T extends TestDataRecord> TestData<T> testData(Class<T> clazz) {
        return pool().testData(clazz);
    }

    public static <T extends TestDataRecord> TestData<T> exclusiveTestData(Class<T> clazz) {
        return pool().exclusiveTestData(clazz);
    }


}
