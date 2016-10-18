package com.automationrockstars.bmo.event.processor.internal;

import com.automationrockstars.bmo.event.processor.Message;

public interface MessageProcessor {

	void process(Message message);
	String key();
	
}