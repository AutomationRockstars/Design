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

import com.google.common.base.Predicate;

public interface TestDataPool {

    String poolName();

    void loadFrom(String location);

    void loadFrom(String... locations);

    void close();

    <T extends TestDataRecord> TestData<T> testData(Class<T> clazz);

    <T extends TestDataRecord> TestData<T> exclusiveTestData(Class<T> clazz);

    static interface TestDataPermutator<T extends TestDataRecord> {
        TestData<T> build(Class<T> recordType);

        TestData<T> buildExclusive(Class<T> recordType);

        <V extends TestDataRecord> TestDataPermutator<T> combine(Class<V>... recordTypes);

        TestDataPermutator<T> exclude(Predicate<T> predicate);
    }


}
