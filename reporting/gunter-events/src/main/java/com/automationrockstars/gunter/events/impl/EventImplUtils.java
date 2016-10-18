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
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.environment.EnvironmentUnderTest;
import com.automationrockstars.gunter.events.environment.impl.EnvironmentBrokenImpl;
import com.automationrockstars.gunter.events.environment.impl.EnvironmentWorkingImpl;
public class EventImplUtils {

	public static Class<? extends Event> getClassForType(EventType type){
		switch (type) {
		case EXECUTION_START:
			return TestExecutionStartImpl.class;
		case EXECUTION_FINISH:
			return TestExecutionFinishImpl.class;
			
		case TEST_SUITE_START:
			return TestSuiteStartImpl.class;
			
		case TEST_SUITE_FINISH:
			return TestSuiteFinishImpl.class;
			
		case TEST_CASE_START:
			return TestCaseStartImpl.class;
			
		case TEST_CASE_FINISH:
			return TestCaseFinishImpl.class;
			
		case TEST_STEP_START:
			return TestStepStartImpl.class;
		
		case TEST_STEP_FINISH:
			return TestStepFinishImpl.class;
			
		case ACTION:
			return ActionImpl.class;
			
		case TEST_LOG:
			return LogImpl.class;
			
		case JOB_SCHEDULED:
			return JobScheduledImpl.class;
		case ATTACHMENT:
			return AttachmentImpl.class;
		case COMMIT:
			return CommitImpl.class;
		case ENVIRONMENT_BROKEN:
			return EnvironmentBrokenImpl.class;
		case ENVIRONMENT_UNDER_TESTS:
			return EnvironmentUnderTest.class;
		case ENVIRONMENT_WORKING:
			return EnvironmentWorkingImpl.class;
		case SAMPLE:
			return SampleImpl.class;
		default:
			throw new IllegalArgumentException(String.format("Type %s unrecognized",type));
		}
	}
}
