/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gunter.rabbit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.events.EventBus;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.EventListener;
import com.automationrockstars.gunter.events.Event;
import com.google.common.collect.Maps;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
public class EventBrokerTest {

	Event event;
	@Ignore
	@Test
	public void test() throws Exception  {
		ConfigLoader.config().setProperty(RabbitEventBroker.HOST_PROP, "10.64.1.181");
		ConfigLoader.config().setProperty(RabbitEventBroker.PORT_PROP, "5672");
		ConfigLoader.config().setProperty(RabbitEventBroker.USER_PROP, "guest");
		ConfigLoader.config().setProperty(RabbitEventBroker.PASS_PROP, "guest");
		
		Thread listener = new Thread( new Runnable() {
			
			@Override
			public void run() {
				try {
					event = RabbitEventBroker.defaultConsumer().consume();
				} catch (ShutdownSignalException | ConsumerCancelledException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		listener.start();
//		try {
//		EventBroker.consumer("jobs", "").registerListener(new EventListener() {
//			
//			@Override
//			public void onEvent(TestEvent event) {
//				System.out.println("ev: " + event);
//				
//			}
//		});
//		} catch (Throwable e){
//			System.out.println("Canno register listener on jobs" + e);
//		}
		Map<String,Object> params = Maps.newHashMap();
		params.put("foo", "uolabolga");
		params.put("text", "dsfsdfsd");
		params.put("bool", true);
		
		EventBus.fireEvent(EventFactory.toJson(EventFactory.createJobScheduled("ala", params)));
		RabbitEventBroker.publisher("testing-events", "").fireEvent(EventFactory.toJson(EventFactory.createJobScheduled("SonarQube of TMB BOI Sustatinability",null)));
		for (int i = 0;i<42;i++){
		EventBus.fireEvent(EventFactory.toJson(EventFactory.createExecutionStart("lame")));
		}
		Thread.sleep(5000);
		
		
		assertThat(event, notNullValue());
		System.out.println("Done");
		System.out.println(event);
	}
	@Ignore
	@Test
	public void listen() throws InterruptedException{
		ConfigLoader.config().setProperty(RabbitEventBroker.HOST_PROP, "10.64.1.181");
		ConfigLoader.config().setProperty(RabbitEventBroker.PORT_PROP, "5672");
		ConfigLoader.config().setProperty(RabbitEventBroker.USER_PROP, "guest");
		ConfigLoader.config().setProperty(RabbitEventBroker.PASS_PROP, "guest");
		
		RabbitEventBroker.defaultConsumer().registerListener(new EventListener() {
			
			@Override
			public void onEvent(Event event) {
				System.out.println(event);
				
			}
		});
		
		Thread.sleep(1000000000);
	}

}
