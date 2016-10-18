package com.automationrockstars.gir.data.impl;

import java.util.Map;

import com.automationrockstars.gir.data.TestDataRecord;

public interface TestDataRecordBuilder {

	TestDataRecordBuilder with(String name, Object value);
	TestDataRecordBuilder with(Map<String,?> values);
	TestDataRecordBuilder with(TestDataRecord values);
	
	TestDataRecord record();
	
}
