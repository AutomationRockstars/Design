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
package com.automationrockstars.base;

import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

public class ConfigLoaderTest {

    @Test
    public void getConfig() {
        assertThat(ConfigLoader.config(), is(notNullValue()));
    }

    @Test
    public void should_readFromSystem() {
        System.setProperty("t1", "1");
        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("1")));
        ConfigLoader.config().setProperty("t1", "2");
        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("2")));
    }
}
