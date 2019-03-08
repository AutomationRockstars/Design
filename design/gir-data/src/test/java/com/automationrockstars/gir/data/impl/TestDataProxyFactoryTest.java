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
import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TestDataProxyFactoryTest {

    @Test
    public void should_verifyTestDataRecordClasses() {
        assertThat(TestDataProxyFactory.isTestDataRecord(IsTestDataRecord.class), is(true));
        assertThat(TestDataProxyFactory.isTestDataRecord(NotTestDataRecord.class), is(false));
        assertThat(TestDataProxyFactory.isTestDataRecord(TestDataRecord.class), is(true));
    }

    @Test
    public void should_getPropertyName() {
        assertThat(TestDataProxyFactory.getPropertyName("lowerUpper"), is(equalTo("lower upper")));
        assertThat(TestDataProxyFactory.getPropertyName("UowerUpper"), is(equalTo("uower upper")));
        assertThat(TestDataProxyFactory.getPropertyName("getLowerUpper"), is(equalTo("get lower upper")));
    }

    static interface IsTestDataRecord extends TestDataRecord {

        String id();
    }

    static interface NotTestDataRecord {
        String id();
    }


}
