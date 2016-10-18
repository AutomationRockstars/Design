package com.automationrockstars.bmo.event.processor;

import com.automationrockstars.bmo.event.processor.annotations.ExchangeReceiver;
import com.automationrockstars.bmo.event.processor.annotations.ExchangeSender;
import com.automationrockstars.bmo.event.processor.annotations.Rule;

public class MethodRules {
	
	@Rule("exchange to exchange")
	@ExchangeReceiver("input")
	@ExchangeSender("output")
	public static Message goodExchangeToExchange(Message testEvent){
		return null;
	}
	
	

}
