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

import java.util.Map;

public interface TestExecutionStart extends Event {

	String TE_NAME = "TestExecutionName";

	void setExecutionName(String name);

	String getExecutionName();
	
	void setParameters(Map<String,Object> params);
	Map<String,Object> getParameters();

}