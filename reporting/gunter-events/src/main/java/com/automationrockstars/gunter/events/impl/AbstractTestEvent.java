/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gunter.events.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.IdUtils;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractTestEvent implements Event {
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.TestEventA#getType()
	 */
	public abstract EventType getType();
	
	private String id;
	private String parentId;
	private Date timeStamp;
	
	public AbstractTestEvent(String parentId){
		init(parentId);
	}
	
	private void init(String parent){
		timeStamp = new Date();
		this.parentId = parent;
		if (Strings.isNullOrEmpty(parentId)){
			this.id=IdUtils.id(getType());
		} else {
			this.id=IdUtils.id(parentId, getType());	
		}
	}
	public AbstractTestEvent(){
		init(null);
	}
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.TestEventA#getId()
	 */
	public String getId() {
		return id;
	}
	void setId(String id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.TestEventA#getParentId()
	 */
	public String getParentId() {
		return parentId;
	}
	void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.TestEventA#getTimeStamp()
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public Map<String, Object> attributes() {
		return attributes;
	}
	
	
	private Map<String,Object> attributes = Maps.newHashMap();

	public String toString(){
		return EventFactory.toJson(this);
	}

	@JsonIgnore
	public <T> T getAttribute(String name){
		return (T) attributes().get(name);
	}
	
	@JsonIgnore
	public void setAttribute(String name, String value){
		attributes().put(name,value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! Event.class.isAssignableFrom(obj.getClass()))
			return false;
		return this.id.equals(((Event)obj).getId());
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> T as (Class<T> clazz){
		return (T) this;
		
	};
	
	public Map<String, Object> getAttributes(){
		return Collections.unmodifiableMap(attributes);
	}
	
	public void setAttributes(Map<String,Object> attributes){
		this.attributes = attributes;
	}
	
	public void setAttribute(String attribute,Object value){
		attributes.put(attribute, value);
	}
}
