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
