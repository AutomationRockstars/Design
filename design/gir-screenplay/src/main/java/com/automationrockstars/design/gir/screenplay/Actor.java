package com.automationrockstars.design.gir.screenplay;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.base.JarUtils;
import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.TestDataServices;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath.ResourceInfo;



public class Actor {


	private static Logger LOG = LoggerFactory.getLogger(Actor.class);

	public static Actor forName(String name){
		Actor result = ACTOR_CACHE.get(name);
		if (result == null){
			result = new Actor(name);
			ACTOR_CACHE.put(name, result);
		}
		return result;
	}
	

	private final String name;
	private final AtomicInteger cycle = new AtomicInteger();
	private static final ConcurrentMap<String, Actor> ACTOR_CACHE = Maps.newConcurrentMap();
	
	private Actor(String name){
		Preconditions.checkArgument(supportedActors().contains(name), "Actor %s is not defined. Defined actors: %s",name,supportedActors());
		this.name = name;
		TestDataServices.pool(name).loadFrom(name + ".json");
		
	}

	public int nextCycle(){
		return cycle.incrementAndGet();
	}

	public int cycle(){
		return cycle.get();
	}

	public <T extends TestDataRecord> TestData<T> data(Class<T> type){
		return TestDataServices.pool(name).testData(type);
	}
	
	public <T extends TestDataRecord>  T currentRecordFor(Class<T> type){
		if (TestDataServices.pool(name).testData(type).records().size() > cycle()){
			return TestDataServices.pool(name).testData(type).record(cycle());
		} else {
			return null;
		}
	}

	
	private static Set<String> availableActors = null;
	public static List<String> supportedActors(){
		if (availableActors == null){
			availableActors = Sets.newHashSet();
			String[] actorLocation = ConfigLoader.config().getStringArray("actor.location.parts");
			if (actorLocation == null || actorLocation.length < 1){
				actorLocation = new String[]{"data","actor","json"};
			}
			availableActors.addAll(JarUtils.findResources(actorLocation).transform(new Function<ResourceInfo,String>(){
				@Override
				public String apply(ResourceInfo input) {
					String[] nameParts = input.getResourceName().split("\\.|/"); 
					return nameParts[nameParts.length-2];
				}}).toList());
			LOG.info("Found following actors {}",availableActors);
		}
		return Lists.newArrayList(availableActors);
	}

	
	public Actor perform(Task task){
		LOG.info("{} is performing {}", name,task.getClass().getSimpleName());
		task.performAs(this);
		return this;
	}

	
	public Actor ask(Question question) throws AssertionError {
		LOG.info("{} is asking {}", name,question.getClass().getSimpleName());
		question.askAs(this);
		return this;
	}

		
	
}
