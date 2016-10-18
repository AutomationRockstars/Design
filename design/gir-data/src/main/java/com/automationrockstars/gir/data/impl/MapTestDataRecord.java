package com.automationrockstars.gir.data.impl;

import java.util.Collections;
import java.util.Map;

import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.base.Joiner;

public class MapTestDataRecord implements TestDataRecord{

	
	private final Map<String,?> data;
	
	public MapTestDataRecord(Map<String,?> data) {
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String name) {
		return (T) data.get(name);
	}

	@Override
	public Map<String, ?> toMap() {
		return Collections.unmodifiableMap(data);
	}
	
	public boolean equals(Object o){
		if (o instanceof TestDataRecord){
			return this.toMap().equals(((TestDataRecord) o).toMap());
		}
		return false;
	}
	
	public String toString(){
		return Joiner.on("\n").withKeyValueSeparator(": ").join(data);
	}
}
