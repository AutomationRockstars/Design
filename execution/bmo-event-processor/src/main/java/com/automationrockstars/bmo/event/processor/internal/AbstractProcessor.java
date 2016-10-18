package com.automationrockstars.bmo.event.processor.internal;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.bmo.event.processor.Message;
import com.automationrockstars.bmo.event.processor.annotations.Rule;

public abstract class AbstractProcessor implements MessageProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessor.class);
	private final Method rule;
	private final String key;
	public AbstractProcessor(Method rule, String key){
		this.rule = rule;
		this.key = key;
	}
	@Override
	public void process(Message message) {
		try {
				ResultSender.process(rule.invoke(null, message), rule);
		} catch (Throwable e) {
			LOG.error("Processing {} with {} using processor {} failed due to",message, rule,key,e);
		}
	}

	@Override
	public String key() {
		return key;
	}
	
	public String toString(){
		return String.format("Processor %s for rule %s", key,rule.getAnnotation(Rule.class).value());
	}

	
}
