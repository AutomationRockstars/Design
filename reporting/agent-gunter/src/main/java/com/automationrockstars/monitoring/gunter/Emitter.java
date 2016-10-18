package com.automationrockstars.monitoring.gunter;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;

public class Emitter {

	private static String hostname = null;
	private static String hostname(){
		if (hostname == null){
			try {
				Process p = Runtime.getRuntime().exec("hostname");
				p.waitFor();
				hostname = IOUtils.toString(p.getInputStream()).replaceAll("\\n|\\r", "");
			} catch (InterruptedException | IOException e) {
				hostname = null;
			}
		}
		return hostname;
	}
	public static synchronized void send(final String type,final Map<String,Number> sample){
		try {
			RabbitEventBroker.publisher("monitoring", "*").fireEvent(EventFactory.toJson(
					EventFactory.createSample(hostname(), type, sample)));
		} catch (Exception e) {
		}}
	
	public static synchronized void close(){
		RabbitEventBroker.closeAll();
	}

}
