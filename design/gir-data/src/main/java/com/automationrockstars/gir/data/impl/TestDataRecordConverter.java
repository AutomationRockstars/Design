package com.automationrockstars.gir.data.impl;

import java.util.Map;

import org.apache.commons.beanutils.converters.AbstractConverter;

import com.automationrockstars.gir.data.TestDataRecord;

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
			if (type.isAssignableFrom(value.getClass())){
				return (T) value;
			} 
			if (TestDataRecord.class.isAssignableFrom(value.getClass())){
				value = ((TestDataRecord)value).toMap();
			}
			return (T) TestDataProxyFactory.createProxy(new MapTestDataRecord((Map<String, ?>) value), (Class<? extends TestDataRecord>)type);
		}
		else return null;
	}
}
