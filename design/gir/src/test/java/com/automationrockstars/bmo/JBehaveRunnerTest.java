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
package com.automationrockstars.bmo;

import org.junit.BeforeClass;
import org.junit.Test;

import com.automationrockstars.base.ConfigLoader;

public class JBehaveRunnerTest {
	
	@Test
	public void should_executeFeaturesAndPass() {
		ConfigLoader.config().setProperty("bdd.story.files", "pass");
		ConfigLoader.config().setProperty("bdd.steps.package", "com.automationrockstars");
		JBehaveRunner.executeFeatures();
	}

	@BeforeClass
	public static void prepare(){
		ConfigLoader.config().setProperty("bdd.steps.package", "com.automationrockstars");
	}
	@Test
	public void should_executeFeaturesAndFail(){
		try {
			ConfigLoader.config().setProperty("bdd.story.files", "fail");
			ConfigLoader.config().setProperty("bdd.steps.package", "com.automationrockstars");
			JBehaveRunner.executeFeatures();
			throw new AssertionError("It didnt fail");
		} catch (org.jbehave.core.embedder.Embedder.RunningStoriesFailed e){
			

		}
	}

	@Test
	public void should_executeFeatureAndReport(){
			ConfigLoader.config().setProperty("bdd.story.files", "story");
			ConfigLoader.config().setProperty("bdd.steps.package", "com.automationrockstars");
			JBehaveRunner.executeFeatures();
	
	}

	@Test
	public void should_verifyCornerCases(){
		ConfigLoader.config().setProperty("bdd.story.files", "feature");
		ConfigLoader.config().setProperty("bdd.steps.package", "com.automationrockstars");
		JBehaveRunner.executeFeatures();
		
	}

}
