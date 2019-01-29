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

import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.assertThat;

public class NullConverterTest {

    @Test
    public void test() {
        Integer val = NullConverter.nullConvert(int.class);
        assertThat("int", val == 0);
        byte b = NullConverter.nullConvert(byte.class);
        String nullString = NullConverter.nullConvert(String.class);

    }

}
