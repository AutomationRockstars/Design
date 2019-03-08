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
import com.google.common.collect.Maps;

import java.util.Map;

public class MapTestDataRecordBuilder implements TestDataRecordBuilder {


    private final Map<String, Object> data = Maps.newHashMap();
    private final MapTestDataRecord record = new MapTestDataRecord(data);

    @Override
    public TestDataRecord record() {
        return record;
    }

    @Override
    public TestDataRecordBuilder with(String name, Object value) {
        data.put(name, value);
        return this;
    }

    @Override
    public TestDataRecordBuilder with(Map<String, ?> values) {
        if (values != null) {
            data.putAll(values);
        }
        return this;
    }

    @Override
    public TestDataRecordBuilder with(TestDataRecord values) {
        if (values != null) {
            data.putAll(values.toMap());
        }
        return this;
    }


}
