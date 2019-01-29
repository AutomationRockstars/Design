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

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.TestDataService;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SimpleTestDataService implements TestDataService {

    public static final String NAME = "DEFAULT";
    private static final Logger LOG = LoggerFactory.getLogger(SimpleTestDataService.class);
    private final Map<Class<? extends TestDataRecord>, TestData<? extends TestDataRecord>> sharedTestDataCache = Maps.newConcurrentMap();
    //cache disabled
    // is sharing data disabled as well?
    private final Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> RECORDS_CACHE = Maps.newConcurrentMap();

    @Override
    public void close() throws IOException {
        Files.write(new GsonBuilder().setPrettyPrinting().create().toJson(RECORDS_CACHE), Paths.get("output.json").toFile(), Charset.defaultCharset());
    }

    private void populate(TestData<? extends TestDataRecord> testData, List<Map<String, Object>> dataToBeAdded) {
        if (dataToBeAdded != null) {
            for (Map<String, Object> dataEntry : dataToBeAdded) {
                testData.addNew().with(dataEntry);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends TestDataRecord> TestData<T> getShared(Class<T> type) {
        TestData<T> result = (TestData<T>) sharedTestDataCache.get(type);
        if (result == null) {
            result = new SimpleTestData<>(true, type);
            sharedTestDataCache.put(type, result);
            populate(result, RECORDS_CACHE.get(type));
        }
        return result;
    }

    @Override
    public <T extends TestDataRecord> TestData<T> provide(String name, boolean shared, Class<T> type) {
        if (SimpleTestData.NAME.equals(name)) {
            if (shared) {
                return getShared(type);
            } else {
                TestData<T> result = new SimpleTestData<>(false, type);
                populate(result, RECORDS_CACHE.get(type));
                return result;
            }
        } else return null;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void loadFrom(String file) {
        try {
            RECORDS_CACHE.putAll(DataLoader.loadFrom(file));
        } catch (IOException e) {
            LOG.error("Reading {} failed", file, e);
        }

    }

    @Override
    public void loadFrom(InputStream content) {
        try {
            RECORDS_CACHE.putAll(DataLoader.loadFrom(content));
        } catch (IOException e) {
            LOG.error("Reading failed", e);
        }

    }


}
