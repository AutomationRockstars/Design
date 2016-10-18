package com.automationrockstars.gunter.events.environment.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.environment.EnvironmentUnderTest;

public class EnvironmentUnderTestImpl extends AbstractEnvironmentImpl implements EnvironmentUnderTest {

	
	
	public EnvironmentUnderTestImpl(String parentId, String name) {
		super(parentId, name);
	}

	@Override
	public EventType getType() {
		return EventType.ENVIRONMENT_UNDER_TESTS;
	}

}
