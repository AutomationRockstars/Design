package com.automationrockstars.gir.data;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gir.data.impl.DataLoader;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;

public class TestDataServices {

	private static final List<TestDataService> serviceRegistry = Lists.newCopyOnWriteArrayList();

	static class TestDataServiceBuilder {
		private final String name;
		private boolean shared = true;
		public TestDataServiceBuilder(String name) {
			this.name = name;
		}

		public TestDataServiceBuilder shared(){
			this.shared = true;
			return this;
		}

		public TestDataServiceBuilder exclusive(){
			this.shared = false;
			return this;
		}

		public TestData<? extends TestDataRecord> build(){
			return findTestData(name,shared,TestDataRecord.class);
		}

	}
	public static TestDataServiceBuilder forName(String name){
		return new TestDataServiceBuilder(name);
	}
	private static final WriteLock servicesLock = new ReentrantReadWriteLock().writeLock();
	private static <T extends TestDataRecord> TestData<T> findTestData (String name,boolean shared,Class<T> type){
		servicesLock.lock();
		TestData<T> result = null;
		try {
			Iterator<TestDataService> services = serviceRegistry.iterator(); 
			while (result == null && services.hasNext()){
				result = services.next().provide(name, shared,type);
			}
			return result;
		} finally {
			servicesLock.unlock();
		}
	}

	public static void register(TestDataService service){
		servicesLock.lock();
		try {
			if (! serviceRegistry.contains(service) ){
				serviceRegistry.add(service);
			}} finally {
				servicesLock.unlock();
			}
	}

	static {
		loadFromClasspath();
	}
	private static void loadFromClasspath(){
		for (Class<? extends TestDataService> serviceClass : new Reflections(new ConfigurationBuilder().forPackages(packagesToScan())).getSubTypesOf(TestDataService.class)){
			try {
				register(serviceClass.newInstance());
			} catch (IllegalAccessException | InstantiationException e) {
				LOG.error("Registering test data service {} failed",serviceClass,e);
			}
		};
	}


	public static void close(){
		servicesLock.lock();
		try {
			for (TestDataService service : serviceRegistry){
				try {
					service.close();
				} catch (IOException e) {
				}
			}
			serviceRegistry.clear();
		} finally {
			servicesLock.unlock();
		}
	}

	public static TestData<TestDataRecord> testData(){
		return testData(TestDataRecord.class);
	}

	public static <T extends TestDataRecord> TestData<T> testData(Class<T> type){
		return (TestData<T>) findTestData("DEFAULT",true,type);
	}
	private static final Logger LOG = LoggerFactory.getLogger(TestDataServices.class);
	public static synchronized void loadFrom(String jsonFile){
		try {
			DataLoader.loadFrom(jsonFile);
		} catch (JsonSyntaxException | IOException e) {
			LOG.error("Cannot load data from file {}",jsonFile,e);
			Throwables.propagate(e);
		}
	}

	public static String[] packagesToScan(){
		String[] result = ConfigLoader.config().getStringArray("data.packages");
		final AtomicInteger lastValidClass = new AtomicInteger(0);
		if (result == null || result.length == 0){
			FluentIterable<String> classes = FluentIterable.from(Lists.reverse(Lists.newArrayList(Thread.currentThread().getStackTrace()))).transform(new Function<StackTraceElement, String>() {

				@Override
				public String apply(StackTraceElement input) {
					return input.getClassName();
				}
			});
			classes.firstMatch(new Predicate<String>(){

				@Override
				public boolean apply(String input) {
					lastValidClass.incrementAndGet();
					return input.startsWith(TestDataServices.class.getName());
				}});
			result = classes.limit(lastValidClass.get()).transform(new Function<String, String>() {

				@Override
				public String apply(String input) {
					return String.format("%s.%s",input.split("\\.")[0],input.split("\\.")[1]);
				}
			}).toArray(String.class);
		}
		return result;
	}
}
