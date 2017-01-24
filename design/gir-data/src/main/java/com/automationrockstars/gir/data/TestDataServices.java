package com.automationrockstars.gir.data;

import java.util.concurrent.ConcurrentMap;

import com.automationrockstars.gir.data.impl.SimpleTestDataPool;
import com.google.common.collect.Maps;

public class TestDataServices {


	private static final ConcurrentMap<String,TestDataPool> POOLS = Maps.newConcurrentMap();
	private static final String GLOBAL_POOL = "GLOBAL"; 

	
	public static synchronized TestDataPool pool(String poolName){
		TestDataPool pool = POOLS.get(poolName);
		if (pool == null){
			pool = new SimpleTestDataPool(poolName);
			POOLS.put(poolName, pool);
		}
		return pool;
	}
	
	public static TestDataPool pool(){
		return pool(GLOBAL_POOL);
	}
	
	public static synchronized void loadFrom(String jsonFile){
			pool().loadFrom(jsonFile);
	}
	 
	
	public static void close(){
		pool().close();
	}
	

	public static <T extends TestDataRecord> TestData<T> testData(Class<T> clazz){
		return pool().testData(clazz);
	}
	public static <T extends TestDataRecord> TestData<T> exclusiveTestData(Class<T> clazz){
		return pool().exclusiveTestData(clazz);
	}

	
}
