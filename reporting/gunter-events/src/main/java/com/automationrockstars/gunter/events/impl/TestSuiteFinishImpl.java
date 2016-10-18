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
import com.automationrockstars.gunter.events.TestSuiteFinish;
import com.automationrockstars.gunter.events.TestSuiteStart;

public class TestSuiteFinishImpl extends TestSuiteStartImpl implements TestSuiteFinish{

	public TestSuiteFinishImpl(TestSuiteStart parent) {
		super(parent.getId());
		super.setTestSuiteName(parent.getTestSuiteName());
	}

	public TestSuiteFinishImpl() {
		super();
	}
	private static final String TS_STATUS= "TestSuiteStatus";
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.TestSuiteFinish#setStatus(java.lang.String)
	 */
	public void setStatus(String status){
		attributes().put(TS_STATUS, status);
	}
	
	/* (non-Javadoc)
	 * @see com.automationrockstars.gunter.events.impl.TestSuiteFinish#getStatus()
	 */
	public String getStatus(){
		return getAttribute(TS_STATUS);
	}
	
	@Override
	public EventType getType(){
		return EventType.TEST_SUITE_FINISH;
	}
}
