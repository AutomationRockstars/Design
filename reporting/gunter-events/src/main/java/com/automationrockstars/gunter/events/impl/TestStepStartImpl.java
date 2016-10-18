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
import com.automationrockstars.gunter.events.TestStepStart;

public class TestStepStartImpl extends AbstractTestEvent implements TestStepStart{

	public TestStepStartImpl() {
		super();
	}
	public TestStepStartImpl(String id) {
		super(id);
	}

	@Override
	public void setName(String name) {
		attributes().put(TS_NAME, name);
	}

	private static final String TS_NAME = "TestStepName";
	@Override
	public String getName() {
		return getAttribute(TS_NAME);
	}

	@Override
	public EventType getType() {
		return EventType.TEST_STEP_START;
	}

}
