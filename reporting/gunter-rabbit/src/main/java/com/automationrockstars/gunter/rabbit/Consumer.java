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

import java.io.IOException;
import java.lang.Thread.State;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.EventListener;
import com.automationrockstars.gunter.events.Event;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class Consumer {

	
	private String queue;
	private boolean continoue = true;
	QueueingConsumer consumer ;
	private static final long WAIT_FOR_MESSAGE = ConfigLoader.config().getLong("rabbitmq.message.timeout", 100);
	private final static Logger LOG = LoggerFactory.getLogger(Consumer.class);
	public void stop(){
		continoue = false;
	}
	public void start(){
		continoue = true;
	}
	protected Consumer(String exchange, String routingKey){
		Channel channel = RabbitEventBroker.getChannel();
		try {
			queue = channel.queueDeclare().getQueue();
			RabbitEventBroker.declareExchange(exchange);
			channel.queueBind(queue, exchange, routingKey);
		} catch (IOException e) {
			LOG.error("Cannot bind to exchange {}",exchange,e);
		}
		consumer = new QueueingConsumer(channel);
		try {
			String tag = channel.basicConsume(queue, true, consumer); 
			LOG.info("Consumer tag {}",tag);
		} catch (IOException e) {
			LOG.error("Cannot start consumer {}",e);
		}
	}

	private final List<EventListener> listeners = Lists.newArrayList();
	public void registerListener(EventListener listener){
		listeners.add(listener);
		if (! continoue){
			continoue = true;
		}
		listen();
			
	}
	public void removeListener(EventListener listener){
		listeners.remove(listener);
	}

	private Thread listenerThread = new Thread(new Runnable(){
		@Override
		public void run() {
			while (continoue && listeners.size()  > 0 ){//&& consumer.getChannel().isOpen()) {
				QueueingConsumer.Delivery delivery;
				try {
					
					delivery = consumer.nextDelivery(WAIT_FOR_MESSAGE);
					if (delivery != null){
						String message = new String(delivery.getBody());
						LOG.debug("Received '{}'",message);
//						RabbitEventBroker.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
						
						Event ev = EventFactory.fromJson(message);
						
						for (EventListener listener : listeners){
							try { 
							listener.onEvent(ev);
							} catch (Throwable t){
								LOG.debug("Error in listener {} on event {}. {}",listener,ev,t.toString());
							}
						}

					}
				} catch (ShutdownSignalException e) {
					LOG.info("Shutting down");
					stop();
				} catch (Exception   e) {
					LOG.error("Consuming is interrupted due to {}",e.getMessage());
					LOG.trace("Details",e);
//				} catch ( IOException ack ){
//					LOG.error("Failed to ACK message due to {}",ack.getMessage());
//					LOG.trace("Details",ack);
				}

			}
			LOG.info("Exiting as {} {} {}",continoue,listeners.size()  > 0 ,consumer.getChannel().isOpen());
			LOG.debug("Finishing listener thread");
		}
	
	},"gunter-rabbit-listener");
 
	private void listen(){
		LOG.info("Listener th {} {}",listenerThread,listenerThread.getState());
		if (listenerThread.getState() == State.NEW || listenerThread.getState() == State.RUNNABLE || listenerThread.getState() == State.TERMINATED){
		listenerThread.setDaemon(true);
		listenerThread.start();
			LOG.debug("Started listener thread {}",listenerThread);
		} else {
			LOG.debug("Listener thread {} is already running",listenerThread);
		}
			
	}
	private Event lastEvent;
	private final Object semaphore = new Object();
	
	private EventListener singleMessageListener(){
		return new EventListener(){ 	
			@Override
			public void onEvent(Event event) {
				synchronized(semaphore){	
					lastEvent = event;
					semaphore.notifyAll();
				}
			}};
	}

	public Event consume(){
		EventListener single = singleMessageListener();
		this.registerListener(single);
		try {
			synchronized (semaphore) {
				semaphore.wait();	
			}
		} catch (InterruptedException e) {
			LOG.error("Consuming interrupted",e);
		}
		this.removeListener(single);
		return lastEvent;
	}
	
	public void close(){
		
	}

}
