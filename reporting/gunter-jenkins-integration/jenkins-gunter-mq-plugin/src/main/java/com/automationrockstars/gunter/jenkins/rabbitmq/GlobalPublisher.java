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
package com.automationrockstars.gunter.jenkins.rabbitmq;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.TestExecutionStart;
import com.automationrockstars.gunter.rabbit.Publisher;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;

public class GlobalPublisher {


	private static final Logger LOG = Logger.getLogger(GlobalPublisher.class.getName());
	private static  Publisher publisher;
	
	public static Publisher publisher(){
		if (publisher == null ){
			try {
			publisher = RabbitEventBroker.defaultPublisher();
			} catch (Throwable e){
				LOG.log(Level.SEVERE, "Cannnot connect to RabbitMQ", e);
				publisher = null;
			}
		}
		return publisher;
	}
	public static TestExecutionStart jobStarted(String jobName){
		
		LOG.log(Level.INFO, "Sending event " + jobName);
		TestExecutionStart parent = EventFactory.createExecutionStart(jobName);
		if(publisher()!=null){
			publisher().fireEvent(EventFactory.toJson(parent));
			LOG.log(Level.INFO, "Event sent");
		}
		
		return parent;
	}
	public static void jobFinished(TestExecutionStart parent, String jobResult){
		LOG.log(Level.INFO, "Sending event " + jobResult);
		if (publisher() != null){
			publisher().fireEvent(EventFactory.toJson(EventFactory.createExecutionFinish(parent, jobResult)));
		}
	}
}
