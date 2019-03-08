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

import java.util.Map;

public interface TestDataRecord {

    <T> T get(String name);

    Map<String, ?> toMap();

    <C extends TestDataRecord> C modify(String name, Object value);

    <C extends TestDataRecord> C modify(C original);
}
