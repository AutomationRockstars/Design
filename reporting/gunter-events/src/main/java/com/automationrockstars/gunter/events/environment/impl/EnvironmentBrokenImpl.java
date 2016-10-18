package com.automationrockstars.gunter.events.environment.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.environment.EnvironmentBroken;

public class EnvironmentBrokenImpl extends AbstractEnvironmentImpl implements EnvironmentBroken{

	public EnvironmentBrokenImpl(String parentId, String name, String cause) {
		super(parentId, name);
		setCause(cause);
	}
	private static final String CAUSE = "CAUSE";
	@Override
	public String getCause() {
		return getAttribute(CAUSE);
	}

	public void setCause(String cause){
		attributes().put(CAUSE, cause);
	}
	@Override
	public EventType getType() {
		return EventType.ENVIRONMENT_BROKEN;
	}

}
