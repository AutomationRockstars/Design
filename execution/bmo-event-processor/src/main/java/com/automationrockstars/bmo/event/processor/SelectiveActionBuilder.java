package com.automationrockstars.bmo.event.processor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.impl.EventImplUtils;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class SelectiveActionBuilder {

	List<Map.Entry<Predicate<Event>,Action<Event>>> actions = Lists.newArrayList();
	
	public static SelectiveActionBuilder newRule(){
		return new SelectiveActionBuilder();
	}
	private Predicate<Event> currentEventType;
	private static Predicate<Event> createClassPredicate(final Class<? extends Event> clazz){
		return new Predicate<Event>() {

			@Override
			public boolean apply(Event input) {
				return ArrayUtils.contains(input.getClass().getInterfaces(),clazz);
			}
		};
	}
	
	public static Predicate<Event> predicate(final boolean apply){
		return new Predicate<Event>() {

			@Override
			public boolean apply(Event input) {
				return apply;
			}
		};
	}
	public SelectiveActionBuilder on(Class<? extends Event> eventType){
		currentEventType = createClassPredicate(eventType);
		return this;
	}
	
	public SelectiveActionBuilder onOthers() {
		on(EventType.ALL);
		return this;
	}
	
	public SelectiveActionBuilder ignore() {
		run(Action.DO_NOTHING);
		return this;
	}
	public SelectiveActionBuilder store() {
		run(Action.STORE);
		return this;
	}
	public SelectiveActionBuilder passTrough() {
		run(Action.PASS_TROUGH);
		return this;
	}
	@SuppressWarnings("unchecked")
	public SelectiveActionBuilder on(EventType type) {
		if (type.equals(EventType.ALL)) {
			currentEventType = predicate(true);
		} else {
			currentEventType = createClassPredicate((Class<? extends Event>) EventImplUtils.getClassForType(type).getInterfaces()[0]);
		}
		return this;
	}
	
	public SelectiveActionBuilder on(Predicate<Event> check){
		currentEventType = check;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public SelectiveActionBuilder run(Action<? extends Event> action){
		actions.add(Collections.singletonMap(currentEventType,(Action<Event>) action).entrySet().iterator().next());
		return this;
	}
	
	public Message process(final Event event){
		Optional<Message> result = FluentIterable.from(actions).filter(new Predicate<Map.Entry<Predicate<Event>,Action<Event>>>() {
			@Override
			public boolean apply(Entry<Predicate<Event>, Action<Event>> input) {
				return input.getKey().apply(event);
			}
		}).transform(new Function<Entry<Predicate<Event>, Action<Event>>,Message>() {
				@Override
				public Message apply(Entry<Predicate<Event>, Action<Event>> input) {
					Message result = input.getValue().process(event); 
					return (result==null)?Message.NULL : result;
				}
			}).first();
		return result.or(Message.NULL);
	}
}
