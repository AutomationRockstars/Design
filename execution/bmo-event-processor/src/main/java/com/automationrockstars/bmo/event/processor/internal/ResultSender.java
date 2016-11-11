package com.automationrockstars.bmo.event.processor.internal;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.bmo.event.processor.HttpEventUtils.RequestProducer;
import com.automationrockstars.bmo.event.processor.Message;
import com.automationrockstars.bmo.event.processor.annotations.Rule;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.rabbit.Publisher;
import com.google.common.base.Preconditions;

public class ResultSender {

	
	private static final Logger LOG = LoggerFactory.getLogger(ResultSender.class);
	public static synchronized void process(Object result, Method method){
		if (result == null) return;
		Preconditions.checkArgument(Message.class.isAssignableFrom(result.getClass()),"Wrong type of object received %s",result.getClass());
		Message message = (Message) result;
		LOG.debug("Received result {} from rule {}",message,method.getAnnotation(Rule.class).value());
		if (message.toHttpContent() != null){
			for (RequestProducer httpSender : RuleReporter.getHttpSenders(method)){
				try {
					HttpResponse resp = httpSender.execute(
							message.toHttpContent()
							).returnResponse(); 		
					LOG.debug("Response from request {}",resp);
				} catch (IOException e) {
					LOG.error("Cannot process message {} with sender {}",message.toHttpContent(),httpSender,e);
				}
			}
		}
		if (message.toEvent() != null){
			for (Publisher eventSender : RuleReporter.getExchangeSenders(method)){
				eventSender.fireEvent(EventFactory.toJson(message.toEvent()));
			}
		}
	}
}
