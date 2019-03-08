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

import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.base.Joiner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapTestDataRecord implements TestDataRecord {


    private final Map<String, Object> data;

    public MapTestDataRecord(Map<String, Object> data) {
        this.data = data;
    }

    public static MapTestDataRecord empty() {
        return new MapTestDataRecord(new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) data.get(name);
    }

    @Override
    public Map<String, ?> toMap() {
        return Collections.unmodifiableMap(data);
    }

    public boolean equals(Object o) {
        if (o instanceof TestDataRecord) {
            return this.toMap().equals(((TestDataRecord) o).toMap());
        }
        return false;
    }

    public String toString() {
        return Joiner.on("\n").withKeyValueSeparator(": ").join(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends TestDataRecord> C modify(String name, Object value) {
        data.put(name, value);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends TestDataRecord> C modify(C original) {
        for (String key : original.toMap().keySet()) {
            data.put(key, original.get(key));
        }
        return (C) this;
    }

}
