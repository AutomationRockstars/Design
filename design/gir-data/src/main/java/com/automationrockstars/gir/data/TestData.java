package com.automationrockstars.gir.data;

import com.automationrockstars.gir.data.impl.TestDataRecordBuilder;
import com.google.common.collect.FluentIterable;

public interface TestData<T extends TestDataRecord> {

	String name();
	
	boolean isShared();

	FluentIterable<T> records();
	
	T record(int cycle);
	
	TestDataRecordBuilder addNew();
	
}
