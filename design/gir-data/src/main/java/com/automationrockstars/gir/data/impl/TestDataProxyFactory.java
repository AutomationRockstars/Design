package com.automationrockstars.gir.data.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;

import org.apache.commons.beanutils.ConvertUtils;

import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.converter.SmartDateConverter;
import com.google.common.base.Preconditions;



public class TestDataProxyFactory {
	public static boolean isTestDataRecord(Class<?> type){
		for (Class<?> sup : type.getInterfaces()){
			if (isTestDataRecord(sup)){
				return true;
			}
		}
		return type.equals(TestDataRecord.class);
	}
	private static final TestDataRecordConverter tdrConverter = new TestDataRecordConverter();
	static {

		SmartDateConverter dateConverter = new SmartDateConverter();
		ConvertUtils.register(dateConverter, Date.class);
		ConvertUtils.register(tdrConverter, TestDataRecordConverter.convertible);
	}

	private static class TestDataBridge implements InvocationHandler{
		private final TestDataRecord data;
		public TestDataBridge(TestDataRecord data){
			this.data = data;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getDeclaringClass().equals(TestDataRecord.class) || method.getDeclaringClass().equals(Object.class)) {
				return method.invoke(data, args);
			}

			Preconditions.checkState(
					!method.getReturnType().equals(Void.TYPE)
					&& (args == null || args.length == 0),
					"Only getter methods are supported in TestDataRecord interface!");
			String propertyName = method.getName();
			Object value = data.get(propertyName);
			if (value == null){
				if (! data.toMap().containsKey(propertyName)){
					propertyName = getPropertyName(propertyName);
				}
				value = data.get(propertyName);
			}

			if (isTestDataRecord(method.getReturnType())){
				return tdrConverter.convert(method.getReturnType(), value);
			} else {
				return (value == null)?value :ConvertUtils.convert(value, method.getReturnType());
			}

		}
	}
	public static String getPropertyName(String name) {
		if (name.startsWith("get")){
			name = name.substring(3);
		}
		return separatedWords(name); 

	}
	public static String separatedWords(String property){
		return property.replaceAll(
				String.format("%s|%s|%s",
						"(?<=[A-Z])(?=[A-Z][a-z])",
						"(?<=[^A-Z])(?=[A-Z])",
						"(?<=[A-Za-z])(?=[^A-Za-z])"
						),
				" "
				).toLowerCase();
	}

	public static <T extends TestDataRecord> T createProxy(TestDataRecord data, Class<T> type){
		if (type == TestDataRecord.class){
			return type.cast(data);
		}
		return type.cast(Proxy.newProxyInstance(TestDataRecord.class.getClassLoader(), new Class[]{type}, new TestDataBridge(data)));
	}
}
