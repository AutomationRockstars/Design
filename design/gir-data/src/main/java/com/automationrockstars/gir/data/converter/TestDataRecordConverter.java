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

package com.automationrockstars.gir.data.converter;

import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.impl.MapTestDataRecord;
import com.automationrockstars.gir.data.impl.TestDataProxyFactory;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.converters.AbstractConverter;

import java.util.Map;

public class TestDataRecordConverter extends AbstractConverter {

    public static Class<? extends TestDataRecord> convertible;

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        return convert(type, value);
    }

    @Override
    protected Class<?> getDefaultType() {
        return convertible;
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(Class<T> type, Object value) {
        if (TestDataProxyFactory.isTestDataRecord(type)) {
            if (value == null) {
                value = MapTestDataRecord.empty();
            }
            if (type.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
            if (TestDataRecord.class.isAssignableFrom(value.getClass())) {
                Map<String, Object> content = Maps.newHashMap();
                content.putAll(((TestDataRecord) value).toMap());
                value = content;
            }
            return (T) TestDataProxyFactory.createProxy(new MapTestDataRecord((Map<String, Object>) value), (Class<? extends TestDataRecord>) type);
        } else return null;
    }
}
