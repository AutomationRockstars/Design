package com.automationrockstars.gunter.events.resource;

import java.util.Map;

import com.automationrockstars.gunter.events.Event;

public interface Resource extends Event{

	String getHostName();
	String getMetricType();
	Map<String,Object> getSample();
	
}
