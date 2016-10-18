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
import com.automationrockstars.gunter.events.TestExecutionStart;
import com.google.common.collect.Maps;

public class TestExecutionStartImpl extends AbstractTestEvent implements TestExecutionStart{

	public TestExecutionStartImpl(String parentId) {
		super(parentId);
	}

	public TestExecutionStartImpl() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.TestExecutionStart#setExecutionName(java.lang.String)
	 */
	public void setExecutionName(String name){
		attributes().put(TE_NAME, name);
	}
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.TestExecutionStart#getExecutinName()
	 */
	public String getExecutionName(){
		return getAttribute(TE_NAME); 
	}
	
	public EventType getType(){
		return EventType.EXECUTION_START;
	}

	private Map<String,Object> parameters = Maps.newHashMap();
	@Override
	public void setParameters(Map<String, Object> params) {
		this.parameters = params;
		
	}

	@Override
	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}




}
