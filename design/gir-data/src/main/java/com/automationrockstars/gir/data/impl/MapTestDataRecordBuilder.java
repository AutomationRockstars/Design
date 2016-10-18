package com.automationrockstars.gir.data.impl;

import java.util.Map;

import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.collect.Maps;

public class MapTestDataRecordBuilder implements TestDataRecordBuilder {

	
	private final Map<String,Object> data = Maps.newHashMap();
	private final MapTestDataRecord record = new MapTestDataRecord(data);

	@Override
	public TestDataRecord record(){
		return record;
	}
	@Override
	public TestDataRecordBuilder with(String name, Object value) {	
		data.put(name, value);
		return this;
	}

	@Override
	public TestDataRecordBuilder with(Map<String, ?> values) {
		data.putAll(values);
		return this;
	}

	@Override
	public TestDataRecordBuilder with(TestDataRecord values) {
		data.putAll(values.toMap());
		return this;
	}


	
}
