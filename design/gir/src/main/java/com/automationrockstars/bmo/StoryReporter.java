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

import java.util.Map;

public interface StoryReporter {

	String name();
		
	void start();
	void finish();
	
	void beforeStory(String name, String description, String path);
	void afterStory();
	
	void beforeScenario(String scenarioTitle);
	void afterScenario();
	
	void example(Map<String, String> tableRow);
	void beforeStep(String step);
	
	void successful(String step);
	void ignorable(String step);
	void pending(String step);
	void notPerformed(String step);
	void failed(String step, Throwable cause);

	void attach(byte[] attachment, String title, String mimeType);
	
	public static class Factory {
		public static StoryReporter reporter(){
			CompositeStoryReporter.load();
			return CompositeStoryReporter.reporter();
		}
	}
}
