package com.automationrockstars.bmo.event.processor;

import java.util.List;
import java.util.Map;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Event;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class EventStorage {

	private Map<String,Event> events = Maps.newHashMap();

	private static final ThreadLocal<EventStorage> stores = new InheritableThreadLocal<EventStorage>(){

		@Override
		protected EventStorage initialValue(){
			return newInstance();
		}

	};
	public static EventStorage storage(){
		return stores.get();
	}
	public static EventStorage newInstance(){
		return new EventStorage();
	}


	public void store(final Event event){
		events.put(event.getId(), event);
	}

	public boolean has(Event event){
		return events.get(event.getId()) != null;
	}
	public void clean(){
		events.clear();
	}
	public FluentIterable<? extends Event> getChildren(Event event, final EventType type){
		return getChildren(event).filter(byType(type));	
	}
	public Event getParent(Event event){
		if (event.getParentId() != null){
			return events.get(event.getParentId());
		} else return null;
	}

	public Event getParent(Event event, EventType type){
		Event parent = getParent(event);
		if (parent == null || parent.getType().equals(type)){
			return parent; 
		} else {
			return getParent(parent, type);
		}
	}

	public FluentIterable<? extends Event> getAllChildren(Event event,final EventType type){
		return getAllChildren(event).filter(byType(type));
	}

	public FluentIterable<? extends Event> getTree(Event event){
		List<Event> tree = Lists.newArrayList(getRootParent(event));

		getAllChildren(tree.get(0)).copyInto(tree);
		return FluentIterable.from(tree);
	}

	public Event getRootParent(Event event){
		Event root = event;
		while(getParent(root)!=null){
			root=getParent(root);
		}
		return root;
	}

	public void remove(Event event){
		events.remove(event.getId());
	}
	public void clearTree(Event event){
		Event root = getRootParent(event);
		for (Event child : getAllChildren(root)){
			remove(child);
		}
		remove(root);
	}
	public FluentIterable<? extends Event> getChildren(final Event event){
		Preconditions.checkNotNull(event, "Event cannot be null");
		return getAll().filter(new Predicate<Event>() {
			@Override
			public boolean apply(Event input) {
				return input != null 
						&&input.getParentId() != null 
						&& input.getParentId()
						.equals(event.getId());
			}
		});

	}
	public FluentIterable<? extends Event> getAllChildren(Event event){
		List<Event> children = Lists.newArrayList();
		if (event != null){
			getChildren(event).copyInto(children);

			for (Event childEvent : getChildren(event)){
				getAllChildren(childEvent).copyInto(children);
			}
		}
		return FluentIterable.from(children);
	}

	public FluentIterable<? extends Event> getAll() {
		return FluentIterable.from(Lists.newArrayList(events.values()));
	}


	public boolean storeIf(Event event, boolean expression){
		if (expression){
			events.put(event.getId(), event);
			return true;
		} else return false;
	}

	public boolean storeIfParentStored(Event event){
		return storeIf(event, hasParent(event));
	}

	static Predicate<Event> byType(final EventType type){
		return new Predicate<Event>() {
			@Override
			public boolean apply(Event input) {
				return input.getType().equals(type);
			}
		};
	}
	public boolean hasEvents(final EventType type){
		return ! getAll().filter(byType(type)).isEmpty();
	}

	public boolean hasParent(Event event) {
		return getParent(event)!= null;
	}

}
