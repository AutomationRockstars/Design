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
package com.automationrockstars.gunter.events;

import java.util.Date;
import java.util.Map;

import com.automationrockstars.gunter.EventType;

public interface Event {

	EventType getType();

	String getId();

	String getParentId();

	Date getTimeStamp();
	
	void setAttributes(Map<String,Object> attributes);
	Map<String, Object> getAttributes();

	<T> T getAttribute(String attribute);

	void setAttribute(String attribute,Object value);
	
	<T extends Event> T as(Class<T> clazz);
}