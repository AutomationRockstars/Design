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

import com.automationrockstars.gir.data.impl.TestDataRecordBuilder;
import com.google.common.collect.FluentIterable;

import java.io.IOException;

public interface TestData<T extends TestDataRecord> {

    String name();

    boolean isShared();

    FluentIterable<T> records();

    T record(int cycle);

    TestDataRecordBuilder addNew();

    String serialize();

    void close() throws IOException;

    void close(String location) throws IOException;


}
