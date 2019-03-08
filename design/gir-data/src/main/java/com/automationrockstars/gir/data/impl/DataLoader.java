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
import com.automationrockstars.base.JarUtils;
import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.automationrockstars.gir.data.TestData.REMOTE_DATA_URL_PROP;


public class DataLoader {


    private static final FluentIterable<Class<? extends TestDataRecord>> AVAILABLE_TYPES = FluentIterable.from(new org.reflections.Reflections(new ConfigurationBuilder().forPackages(TestDataServiceRegistry.packagesToScan())).getSubTypesOf(TestDataRecord.class)).filter(new Predicate<Class<? extends TestDataRecord>>() {
        @Override
        public boolean apply(Class<? extends TestDataRecord> input) {
            return input.isInterface();
        }
    });
    private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);

    private static Class<? extends TestDataRecord> findTestDataRecordClass(final String name) {
        LOG.debug("Looking for type matching {} among {}", name, AVAILABLE_TYPES);
        Optional<Class<? extends TestDataRecord>> result = AVAILABLE_TYPES.firstMatch(new Predicate<Class<? extends TestDataRecord>>() {
            @Override
            public boolean apply(Class<? extends TestDataRecord> input) {
                return TestDataProxyFactory.separatedWords(input.getSimpleName()).equalsIgnoreCase(name);
            }
        });
        if (result.isPresent()) {
            return result.get();
        } else {
            LOG.warn("Tag {} cannot be matched to any type from available {}", name, Joiner.on("\n").join(AVAILABLE_TYPES));
            return null;
        }

    }


    public static Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> loadFrom(String fileName) throws IOException {
        return loadDataResource(fileName);
    }

    private static Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> loadDataResource(String resourceName) {
        if (ConfigLoader.config().containsKey(REMOTE_DATA_URL_PROP)){
            return loadRemoteDataResource(resourceName);
        } else {
            return loadLocalDataResource(resourceName);
        }
    }


    private static Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> loadLocalDataResource(String resourceName) {
        LOG.info("Loading local data");
        try (InputStream content = JarUtils.findResources(resourceName).first().get().url().openStream()) {
            return loadFrom(content);
        } catch (NoSuchElementException | IOException e) {
            LOG.error("Resource {} cannot be used as data", resourceName, e);
        }
        ;
        return null;

    }

    private static Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> loadRemoteDataResource(String resourceName) {
        try {
            URL remoteUrl = new URL(String.format("%s/%s",ConfigLoader.config().getString(REMOTE_DATA_URL_PROP),resourceName));
            LOG.info("Loading data from URL {}",remoteUrl);
            try (InputStream content = remoteUrl.openStream()) {
                return loadFrom(content);
            } catch (NoSuchElementException | IOException e) {
                LOG.error("Remote resource {} cannot be used as data", resourceName, e);
            }
        } catch (MalformedURLException e) {
            LOG.error("Cannot read data from {}",String.format("%s/%s",ConfigLoader.config().getString(REMOTE_DATA_URL_PROP),resourceName),e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> loadFrom(InputStream content) throws IOException {
        Map<String, Object> result = Collections.emptyMap();
        try (Reader data = new InputStreamReader(content)) {
            result = new Gson().fromJson(data, HashMap.class);
        } catch (NoSuchElementException | IOException e) {
            LOG.error("Resource cannot be used as data", e);
        }
        return loadFromData(result);
    }

    @SuppressWarnings({"unchecked"})
    private static Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> loadFromData(final Map<String, Object> data) {
        Map<Class<? extends TestDataRecord>, List<Map<String, Object>>> result = Maps.newHashMap();
        for (String dataType : data.keySet()) {
            LOG.debug("Looking for TestDataRecord spec for data marked with {}", dataType);
            Class<? extends TestDataRecord> testDataType = null;
            if (dataType.endsWith(" list")) {
                testDataType = findTestDataRecordClass(dataType.replace(" list", ""));
            } else {
                testDataType = findTestDataRecordClass(dataType);
            }
            if (testDataType != null) {
                LOG.info("Data tagged with {} will be mapped to {}", dataType, testDataType.getName());
                List<Map<String, Object>> bag = result.get(testDataType);
                if (bag == null) {
                    bag = Lists.newArrayList();
                    result.put(testDataType, bag);
                }
                Object listOrMap = data.get(dataType);
                if (List.class.isAssignableFrom(listOrMap.getClass())) {
                    for (Map<String, Object> entry : (List<Map<String, Object>>) listOrMap) {
                        bag.add(entry);
                    }
                } else {
                    bag.add((Map<String, Object>) listOrMap);
                }
                LOG.trace("Mapped {}", callMappings(testDataType, Iterables.getLast(bag)));
            } else {
                LOG.warn("Data marked with {} cannot be matched to any TestDataRecord specification.", dataType);
            }
        }
        return result;

    }

    @SuppressWarnings("unchecked")
    private static String callMappings(Class<?> type, Object o) {
        StringBuilder result = new StringBuilder();
        for (Method m : type.getMethods()) {
            try {
                if (Lists.newArrayList(m.getDeclaringClass().getInterfaces()).contains(TestDataRecord.class)
                        && !m.getDeclaringClass().equals(TestDataRecord.class)
                        && !Lists.newArrayList(m.getReturnType().getInterfaces()).contains(TestDataRecord.class)) {
                    Object tdr = TestDataProxyFactory.createProxy(new MapTestDataRecord((Map<String, Object>) o), (Class<? extends TestDataRecord>) type);
                    result.append("\n").append(m.getName()).append(": ").append(
                            m.invoke(tdr, (Object[]) null)
                    );
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOG.error("Cannot call mapped method on {} of {}", o, type, e);
            }
        }
        return result.toString();
    }
}
