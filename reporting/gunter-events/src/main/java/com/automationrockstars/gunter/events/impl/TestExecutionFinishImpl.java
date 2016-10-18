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
import com.automationrockstars.gunter.events.TestExecutionFinish;
import com.automationrockstars.gunter.events.TestExecutionStart;

public class TestExecutionFinishImpl extends TestExecutionStartImpl implements TestExecutionFinish {

	public TestExecutionFinishImpl(){
		super();
	}
	public TestExecutionFinishImpl(TestExecutionStart parent) {
		super(parent.getId());
		super.setExecutionName(parent.getExecutionName());
	}

	@Override
	public EventType getType(){
		return EventType.EXECUTION_FINISH;
	}
	
	private static final String TE_STATUS = "TestExecutionStatus";
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.TestExecutionFinish#setStatus(java.lang.String)
	 */
	public void setStatus(String status){
		attributes().put(TE_STATUS, status);
	}
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.TestExecutionFinish#getStatus()
	 */
	public String getStatus(){
		return getAttribute(TE_STATUS);
	}

}
