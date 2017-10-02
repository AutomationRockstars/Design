package com.automationrockstars.gir.data;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

public interface TestDataPool {

	String poolName();
	void loadFrom(String location);
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
