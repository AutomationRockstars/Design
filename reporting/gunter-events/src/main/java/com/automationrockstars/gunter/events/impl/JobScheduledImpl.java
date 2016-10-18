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
import java.util.Map;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.JobScheduled;
import com.google.common.collect.Maps;

public class JobScheduledImpl extends AbstractTestEvent implements JobScheduled {

	private final static String PROJECT_NAME = "ProjectName";
	private static final String PARAMETERS = "PARAMETERS";
	public JobScheduledImpl(){
		super();
	}
	
	
	public void setParameters(Map<String,Object> params){
		
		setAttribute(PARAMETERS, params);
	}
	public Map<String,Object> getParameters(){
		return (getAttribute(PARAMETERS) == null)?Maps.<String,Object>newHashMap(): Collections.unmodifiableMap((Map<String,Object>) getAttribute(PARAMETERS));
		
	}
	
	public void setProjectName(String projectName){
		attributes().put(PROJECT_NAME, projectName);
	}
	public JobScheduledImpl(String projectName) {
		super();
		attributes().put(PROJECT_NAME,projectName);
	}
	
	public JobScheduledImpl(String parentId, String projectName) {
		super(parentId);
		attributes().put(PROJECT_NAME,projectName);
	}
	@Override
	public String getProjectName() {
		return (String) attributes().get(PROJECT_NAME);
	}

	
	
	@Override
	public Object getParameter(String name) {
		return getParameters().get(name);
	}

	@Override
	public void setParameter(String name, Object value){
		getParameters().put(name, value);
	}

	private static final String CAUSE = "cause";
	@Override
	public String getCause() {
		return getAttribute(CAUSE);
	}

	@Override
	public void setCause(String cause) {
		attributes().put(CAUSE, cause);
		
	}

	@Override
	public EventType getType() {
		return EventType.JOB_SCHEDULED;
	}

}
