package com.automationrockstars.gir.data;

public interface TestDataPool {

	String poolName();
	void loadFrom(String location);
	void close();
	<T extends TestDataRecord> TestData<T> testData(Class<T> clazz);
	<T extends TestDataRecord> TestData<T> exclusiveTestData(Class<T> clazz);
	
	
}
