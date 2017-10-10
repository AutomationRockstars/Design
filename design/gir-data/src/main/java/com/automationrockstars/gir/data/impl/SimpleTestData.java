package com.automationrockstars.gir.data.impl;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SimpleTestData<T extends TestDataRecord> implements TestData<T> {

	public static final String NAME = "DEFAULT";
	@Override
	public String name() {
		return NAME;
	}

	private final ConcurrentMap<Class<? extends TestDataRecord>,List<? extends TestDataRecord>> recordBag = Maps.newConcurrentMap();
//	private static final List<? extends TestDataRecord> sharedRecords = Lists.newCopyOnWriteArrayList();
	private final boolean shared; 
	private final List<T> exclusiveRecords;
	private Class<T> classOf;
	@SuppressWarnings({ "unchecked", "hiding" })
	private <T extends TestDataRecord> List<T> get(Class<T> type){
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

	@Override
	public String serialize() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(data().stream().map(record -> unMap(record)).collect(Collectors.toList()));
	}

	public static Map<String,Object> unMap(TestDataRecord inner) {
		Map<String,Object> result = Maps.newHashMap();
		for (Map.Entry<String, ?> entry : inner.toMap().entrySet()) {
			if (TestDataRecord.class.isAssignableFrom(entry.getValue().getClass())) {
				result.put(entry.getKey(), unMap((TestDataRecord) entry.getValue()));
			} else {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		close("output.json");
	}

	@Override
	public void close(String location) throws IOException {
		Files.write(serialize(), Paths.get(location).toFile(), Charset.defaultCharset());
	}

	public String toString(){
		return String.format("TestData of type %s with name %s", classOf,name());
	}
	@Override
	public T record(int cycle) {
		Preconditions.checkState(cycle < data().size(),"%s does not contain at least %s records",this,cycle);
		return records().get(cycle);
	}
	

}
