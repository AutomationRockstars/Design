/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gunter.events.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Action;

public class ActionImpl extends AbstractTestEvent implements Action {

	public ActionImpl(){
		super();
	}
	public ActionImpl(String parentId) {
		super(parentId);
	}

	@Override
	public EventType getType() {
		return EventType.ACTION;
	}
	
	private static final String A_NAME = "actionName";
	private static final String ELEMENT = "element";
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.Action#setAction(java.lang.String, java.lang.String)
	 */
	public void setAction(String actionName, String element){
		attributes().put(A_NAME, actionName);
		attributes().put(ELEMENT, element);
	}
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.Action#getActionName()
	 */
	public String getActionName(){
		return (String) attributes().get(A_NAME);
	}
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.Action#getElement()
	 */
	public String getElement(){
		return (String) attributes().get(ELEMENT);
	}

}
