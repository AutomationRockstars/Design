package com.automationrockstars.gir.data.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;

import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.converter.NullConverter;
import com.automationrockstars.gir.data.converter.SmartDateConverter;
import com.automationrockstars.gir.data.converter.TestDataRecordConverter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;



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
				if (value == null && method.getName().startsWith("get")){
					value = data.get(propertyName.substring(4));	
				}
			}
			Class<?> returnType = method.getReturnType();
			Object result = null;
			if (List.class.equals(method.getReturnType())){				
				result= handleList(method,value);
			} else {
							result = adjustResult(returnType, value);
			}
			if (isTestDataRecord(returnType)){
				if (((TestDataRecord) result).toMap().isEmpty()){
					data.modify(propertyName, result);
				}
			}
			return result;
				

		}
	
		private Object handleList(Method method, Object value){
			Class<?> returnType = method.getReturnType();
		
			Type returnGen = method.getGenericReturnType();
			if (returnGen instanceof ParameterizedType){
				Type actualType = ((ParameterizedType) returnGen).getActualTypeArguments()[0];
				if (actualType instanceof Class){
					returnType = (Class) actualType;
				}
			}
			
			if (value != null && List.class.isAssignableFrom(value.getClass())){
				List val = (List) value;
				if (val.isEmpty()){
					return value;
				} else {
					List result = Lists.newArrayList();
					for (Object part : val){
						result.add(adjustResult(returnType, part));
					}
					return result;
				}
			}
			return Lists.newArrayList(adjustResult(returnType, value));
			
		}
		private Object adjustResult(Class<?> returnType, Object value){
			if (isTestDataRecord(returnType)){
				Object tdr = tdrConverter.convert(returnType, value);
				
				return tdr;
			} else {
				return (value == null || (value.getClass().equals(List.class) && ((List)value).isEmpty()))? NullConverter.nullConvert(returnType) :convert(value, returnType);
			}
		}
		public Object convert(Object value,Class<?> returnType){
			if ( ! ConvertUtils.lookup(Date.class).getClass().equals(SmartDateConverter.class)){
				ConvertUtils.register(new SmartDateConverter(), Date.class);
			}
			return ConvertUtils.convert(value, returnType);
		}
	}
	public static String getPropertyName(String name) {
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
	
	public static <T extends TestDataRecord> T create(Map<String,Object> data,Class<T> clazz){
		return createProxy(new MapTestDataRecord(data),clazz);
	}
}
