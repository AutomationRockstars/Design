package com.automationrockstars.gunter.events;

import java.util.Map;

public interface Sample extends Event{
	
	String getHost();
	void setHost(String host);
	
	String getSampleType();
	void setSampleType(String type);
	
	void setSample(Map<String,Number> content);
	Map<String,Number> getSample();
}
