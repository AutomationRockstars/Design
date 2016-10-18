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
package com.automationrockstars.bmo.console.peer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.bmo.console.traffic.HttpRequestEvent;
import com.automationrockstars.bmo.console.traffic.HttpResponseEvent;
import com.google.common.eventbus.Subscribe;

public class LogTraffic {
	
	private static final Logger LOG = LoggerFactory.getLogger(LogTraffic.class);	
	
	@Subscribe
	public void handleRequest(HttpRequestEvent request){	
		LOG.info("RQ: {}",request);
	}
	
	@Subscribe
	public void handleResponse(HttpResponseEvent response){
		LOG.info("RP: {}",response);
	}

}
