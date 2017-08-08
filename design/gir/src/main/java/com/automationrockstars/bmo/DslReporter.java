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
package com.automationrockstars.bmo;


import static com.automationrockstars.gunter.events.EventFactory.createTestStepStart;
import static com.automationrockstars.gunter.events.EventFactory.toJson;
import static com.automationrockstars.gunter.events.EventStore.getEvent;
import static com.automationrockstars.gunter.events.EventStore.putEvent;

import com.automationrockstars.gunter.events.EventBus;
import com.automationrockstars.gunter.events.TestCaseStart;
import com.automationrockstars.gunter.events.TestStepStart;
import com.google.common.base.MoreObjects;
public class DslReporter {

	public static final void reportExecutionStart(String executionName){
		
	}
	public static final void reportStepStart(Object name){
		TestStepStart event = createTestStepStart(getEvent(TestCaseStart.class), MoreObjects.firstNonNull(name, "UNKNOWN").toString());
		putEvent(event);
		EventBus.fireEvent(toJson(event));
	}
}
