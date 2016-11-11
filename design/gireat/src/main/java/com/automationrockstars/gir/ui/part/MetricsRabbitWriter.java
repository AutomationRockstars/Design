package com.automationrockstars.gir.ui.part;

import static com.automationrockstars.gunter.events.EventFactory.createSample;
import static com.automationrockstars.gunter.events.EventFactory.toJson;

import java.util.Map;

import com.automationrockstars.gunter.rabbit.RabbitEventBroker;

public class MetricsRabbitWriter {

	public static void log(String host, String type, Map<String,Number> data){
		if (! data.isEmpty()){
			RabbitEventBroker.publisher("performance", "*").fireEvent(toJson(createSample("pci", type, data)));
		}
	}
}
