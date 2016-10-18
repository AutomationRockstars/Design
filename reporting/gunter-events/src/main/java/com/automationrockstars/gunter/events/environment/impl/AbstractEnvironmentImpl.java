package com.automationrockstars.gunter.events.environment.impl;

import com.automationrockstars.gunter.events.environment.Environment;
import com.automationrockstars.gunter.events.impl.AbstractTestEvent;

public abstract class AbstractEnvironmentImpl extends AbstractTestEvent implements Environment {

	public AbstractEnvironmentImpl(String parentId, String name){
		super(parentId);
		setName(name);
	}
	private static final String NAME = "NAME";
	@Override
	public String getName() {
		return getAttribute(NAME);

	}
	
	public void setName(String name){
		attributes().put(NAME, name);
	}

	

}
