package com.automationrockstars.gunter.events.impl;

import java.util.Map;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Sample;

public class SampleImpl extends AbstractTestEvent implements Sample{


	
	private String CONTENT= "SAMPLE";
	@Override
	public void setSample(Map<String, Number> content) {
		setAttribute(CONTENT, content);

	}

	@Override
	public Map<String, Number> getSample() {
		return getAttribute(CONTENT);
	}

	@Override
	public EventType getType() {
		return EventType.SAMPLE;
	}

	private static final String HOST = "HOST";
	private static final String TYPE = "SAMPLE_TYPE";
	@Override
	public String getHost() {
		return getAttribute(HOST);
	}

	@Override
	public void setHost(String host) {
		setAttribute(HOST, host);

	}

	@Override
	public String getSampleType() {
		return getAttribute(TYPE);
	}

	@Override
	public void setSampleType(String type) {
		setAttribute(TYPE, type);

	}

}
