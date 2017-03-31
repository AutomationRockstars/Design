package com.automationrockstars.gir.data.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.commons.collections.map.UnmodifiableMap;

import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.collect.Maps;

public class TestDataRecordConverter extends AbstractConverter {

	@Override
	protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
			return convert(type, value);
	}

	public static Class<? extends TestDataRecord> convertible;
	@Override
	protected Class<?> getDefaultType() {
		return convertible;
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Class<T> type, Object value){
		if (TestDataProxyFactory.isTestDataRecord(type)){
			if (value == null){
				value = MapTestDataRecord.empty();
			}
			if (type.isAssignableFrom(value.getClass())){
				return (T) value;
			} 
			if (TestDataRecord.class.isAssignableFrom(value.getClass())){
				Map<String,Object> content = Maps.newHashMap();
				content.putAll(((TestDataRecord)value).toMap());
				value = content;
			}
			return (T) TestDataProxyFactory.createProxy(new MapTestDataRecord((Map<String, Object>) value), (Class<? extends TestDataRecord>)type);
		}
		else return null;
	}
}
