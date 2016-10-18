package com.automationrockstars.gir.data.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.TestDataServices;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;



public class DataLoader {

	private static final Map<Class<? extends TestDataRecord>,TestData<? extends TestDataRecord>> TEST_DATA_CACHE = Maps.newLinkedHashMap();
	public static Map<Class<? extends TestDataRecord>,TestData<? extends TestDataRecord>> cache(){
		return TEST_DATA_CACHE;
	}
	private static final FluentIterable<Class<? extends TestDataRecord>> AVAILABLE_TYPES= FluentIterable.from(new org.reflections.Reflections(new ConfigurationBuilder().forPackages(TestDataServices.packagesToScan())).getSubTypesOf(TestDataRecord.class)).filter(new Predicate<Class<? extends TestDataRecord>>() {
		@Override
		public boolean apply(Class<? extends TestDataRecord> input) {
			return input.isInterface();
		}
	});
	private static final Logger LOG = LoggerFactory.getLogger(DataLoader.class);
	private static Class<? extends TestDataRecord> findTestDataRecordClass(final String name){
		LOG.debug("Looking for type matching {} among {}",name, AVAILABLE_TYPES);
		Optional<Class<? extends TestDataRecord>> result = AVAILABLE_TYPES.firstMatch(new Predicate<Class<? extends TestDataRecord>>() {
			@Override
			public boolean apply(Class<? extends TestDataRecord> input) {
				return TestDataProxyFactory.separatedWords(input.getSimpleName()).equalsIgnoreCase(name); 
			}
		});
		if (result.isPresent()){
			return result.get();
		} else {
			LOG.warn("Tag {} cannot be matched to any type from available {}", name,Joiner.on("\n").join(AVAILABLE_TYPES));
			return null;
		}

	}


	
	public static void loadFrom(String fileName) throws IOException{
		Collection<File> dataFiles = FileUtils.listFiles(Paths.get("").toFile().getAbsoluteFile(), new NameFileFilter(fileName), TrueFileFilter.INSTANCE);
		if (dataFiles.isEmpty()){
			try (InputStream jarContent = ClassLoader.getSystemResourceAsStream(fileName)){
				Preconditions.checkState(! dataFiles.isEmpty() || jarContent != null,"Data file %s cannot be found",fileName);	
				loadFrom(jarContent);
			}
		} else {
			
			LOG.info("Getting data from {}",dataFiles.iterator().next());
			loadFromStringJson(FileUtils.readFileToString(dataFiles.iterator().next()));
		}
	}
	
	public static void loadFrom(InputStream content) throws IOException{		
			loadFromStringJson(IOUtils.toString(content));
	}
	@SuppressWarnings({"unchecked","rawtypes"})
	private static void loadFromStringJson(String jsonContent){
		Map<String,?> data = new Gson().fromJson(jsonContent, HashMap.class);
		for (String dataType : data.keySet()){
			LOG.debug("Looking for TestDataRecord spec for data marked with {}",dataType);
			Class<? extends TestDataRecord> testDataType = null;
			if ((testDataType = findTestDataRecordClass(dataType))!= null){
				LOG.info("Data tagged with {} will be mapped to {}",dataType,testDataType.getName());
				TestData d = TestDataServices.testData(testDataType);
				TEST_DATA_CACHE.put(testDataType, d);
				Object listOrMap = data.get(dataType);
				if (List.class.isAssignableFrom(listOrMap.getClass())){
					for (Map<String,?> entry : (List<Map<String, ?>>) listOrMap){
						d.addNew().with(entry);
					}
				} else {
					d.addNew().with((Map<String,?>)listOrMap);
				}
				LOG.trace("Mapped {}",callMappings(testDataType, d.records().last().get()));
			} else {
				LOG.warn("Data marked with {} cannot be matched to any TestDataRecord specification.",dataType);
			}
		}

	}
	private static String callMappings(Class<?> type, Object o ){
		StringBuilder result = new StringBuilder();
		for (Method m :type.getMethods()){
			try {
				if (Lists.newArrayList(m.getDeclaringClass().getInterfaces()).contains(TestDataRecord.class)
						&&
						! m.getDeclaringClass().equals(TestDataRecord.class))
					result.append("\n").append(m.getName()).append(": ").append(m.invoke(o, null));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOG.error("Cannot call mapped method on {} of {}",o,type);
			}
		}
		return result.toString();
	}
}
