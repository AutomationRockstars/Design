package com.automationrockstars.gunter.events.environment.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.environment.EnvironmentWorking;

public class EnvironmentWorkingImpl extends AbstractEnvironmentImpl implements EnvironmentWorking{

	
	public EnvironmentWorkingImpl(String parentId, String name) {
		super(parentId, name);
	}

	@Override
	public EventType getType() {
		return EventType.ENVIRONMENT_WORKING;
	}

}
