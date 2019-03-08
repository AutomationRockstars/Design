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

import com.automationrockstars.gir.data.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SimpleTestDataPool implements TestDataPool {

    private final String name;
    private final List<TestDataService> services = Lists.newArrayList();

    public SimpleTestDataPool(String poolName) {
        this.name = poolName;
        services.addAll(TestDataServiceRegistry.services());
    }

    @Override
    public String poolName() {
        return name;
    }

    @Override
    public void loadFrom(String location) {
        for (TestDataService service : services) {
            service.loadFrom(location);
        }
    }

    @Override
    public void loadFrom(String... locations) {
        for (String location : locations) {
            loadFrom(location);
        }
    }

    @Override
    public void close() {

        for (TestDataService service : services) {
            try {
                service.close();
            } catch (IOException e) {

            }
        }
        TestDataServices.removePool(name);
    }

    @Override
    public <T extends TestDataRecord> TestData<T> testData(Class<T> clazz) {
        return testData(clazz, true);
    }

    @Override
    public <T extends TestDataRecord> TestData<T> exclusiveTestData(Class<T> clazz) {
        return testData(clazz, false);
    }

    public <T extends TestDataRecord> TestData<T> testData(Class<T> clazz, boolean shared) {
        TestData<T> result = null;
        Iterator<TestDataService> servicesToCheck = services.iterator();
        TestDataService serviceToCheck;
        while (result == null && servicesToCheck.hasNext() && ((serviceToCheck = servicesToCheck.next()) != null)) {
            result = serviceToCheck.provide(SimpleTestData.NAME, shared, clazz);
        }
        return result;
    }


}
