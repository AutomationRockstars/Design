package com.automationrockstars.gir.data.impl;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SimpleTestData<T extends TestDataRecord> implements TestData<T> {

	@Override
	public String name() {
		return "DEFAULT";
	}

	private static final ConcurrentMap<Class<? extends TestDataRecord>,List<? extends TestDataRecord>> recordBag = Maps.newConcurrentMap();
//	private static final List<? extends TestDataRecord> sharedRecords = Lists.newCopyOnWriteArrayList();
	private final boolean shared; 
	private final List<T> exclusiveRecords;
	private Class<T> classOf;
	private static <T extends TestDataRecord> List<T> get(Class<T> type){
		if (recordBag.get(type) == null){
			recordBag.putIfAbsent(type, new CopyOnWriteArrayList<T>());
		}
		return (List<T>) recordBag.get(type);
		
	}
	public SimpleTestData(boolean shared, Class<T> type){
		classOf = type;
		this.shared = shared;
		if (! shared){
			exclusiveRecords = Lists.newArrayList(get(classOf));
		} else {
			exclusiveRecords = null;
		}
	}
	protected List<T> data(){
		if (isShared()){
			return get(classOf);
		} else {
			return exclusiveRecords;
		}
	}
	@Override
	public boolean isShared() {
		return shared;
	}

	@Override
	public FluentIterable<T> records() {
		return (FluentIterable<T>) FluentIterable.from(data());
	}


	@Override
	public TestDataRecordBuilder addNew() {
		MapTestDataRecordBuilder result = new MapTestDataRecordBuilder();
		
		data().add((T) TestDataProxyFactory.createProxy(result.record(),classOf));
		return result;
	}

	public String toString(){
		return String.format("TestData of type %s with name %s", classOf,name());
	}
	@Override
	public T record(int cycle) {
		Preconditions.checkState(cycle < data().size(),"%s does not contain %s records",this,cycle);
		return records().get(cycle);
	}
	

}
