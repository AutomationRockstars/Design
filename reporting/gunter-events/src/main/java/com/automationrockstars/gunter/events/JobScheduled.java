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

public interface JobScheduled extends Event{

	String getProjectName();
	Object getParameter(String name);
	
	void setParameter(String name, Object val);
	
	void setParameters(Map<String,Object> params);
	Map<String,Object> getParameters();
	
	String getCause();
	void setCause(String cause);
}
