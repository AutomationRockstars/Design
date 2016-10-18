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
package com.automationrockstars.gunter.events;

import static com.automationrockstars.gunter.events.EventFactory.*;
import static com.automationrockstars.gunter.events.EventFactory.createExecutionFinish;
import static com.automationrockstars.gunter.events.EventFactory.createExecutionStart;
import static com.automationrockstars.gunter.events.EventFactory.createLog;
import static com.automationrockstars.gunter.events.EventFactory.createSuiteFinish;
import static com.automationrockstars.gunter.events.EventFactory.createSuiteStart;
import static com.automationrockstars.gunter.events.EventFactory.createTestCaseFinish;
import static com.automationrockstars.gunter.events.EventFactory.createTestCaseStart;
import static com.automationrockstars.gunter.events.EventFactory.fromJson;
import static com.automationrockstars.gunter.events.EventFactory.toJson;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;

import com.automationrockstars.gunter.events.impl.TestExecutionStartImpl;

public class EventFactoryTest {

	@Test
	public void executionStart() {
		Event ss = createExecutionStart("test");
		String json = toJson(ss);
		TestExecutionStart isIt = fromJson(json, TestExecutionStartImpl.class);
		String newJson = toJson(isIt);
		assertThat(json,equalTo(newJson));
		
		
	}
	
	@Test
	public void commit(){
		Commit ss = createCommit(null,Collections.singletonMap("user", "alaMaKota"));
		String json = toJson(ss);
		Commit result = fromJson(json);
		assertThat(json,equalTo(toJson(result)));
		System.out.println(json);
	}
	
	
	
	@Test
	public void chain() throws InterruptedException{
		TestExecutionStart execStart = createExecutionStart("chain");
		Thread.sleep(1);
		TestSuiteStart suiteStart = createSuiteStart(execStart, "suiteInChain");
		TestCaseStart tcStart = createTestCaseStart(suiteStart, "tc1");
		Thread.sleep(1);
		Log logEvent = createLog(tcStart, "INFO", "TestClass", "log message");
		Action action = createAction(tcStart, "click", "button");
		TestCaseFinish tcFinish = createTestCaseFinish(tcStart, "PASSED");
		Thread.sleep(1);
		TestSuiteFinish suiteFinish = createSuiteFinish(suiteStart,null);
		TestExecutionFinish execFinish = createExecutionFinish(execStart, null);
		System.out.println(String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n",
				execStart,
				suiteStart,
				tcStart,
				logEvent,
				action,
				tcFinish,
				suiteFinish,
				execFinish));
		
	}

}
