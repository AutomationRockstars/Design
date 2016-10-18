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

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.console.traffic.ConsoleEventBus;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;


public class RequestToNode {

	public static HttpHost getHost(){
		return HttpHost.create(String.format("http://%s:%s",
				ConfigLoader.config().getString("node.ip","127.0.0.1"),
				ConfigLoader.config().getInt("node.port",5555)
				));
	}
	 
	public static final HttpClient client = HttpClients.createDefault();
	private static final Logger LOG = LoggerFactory.getLogger(RequestToNode.class); 

	private static synchronized HttpResponse executeRequest(HttpRequest request){
		HttpResponse response = null;
		try {
			response = client.execute(getHost(),request);
		} catch (IOException e) {
			LOG.error("Cannot execute REST request to node",e);
		}
		return response;
	}
	@Subscribe
	public void handleRequest(HttpRequest request){
		HttpResponse response = executeRequest(request);
		ConsoleEventBus.filter.post(response);
	}

	public static synchronized String getScreenshot(){
		String allContent = null;
		String target = ConfigLoader.config().getString("node.session",SessionHolder.getCurrentTarget());
			Preconditions.checkState(! Strings.isNullOrEmpty(target), "Cannot get active session");
			CloseableHttpResponse respone = (CloseableHttpResponse) executeRequest(new HttpGet("/wd/hub/session/"+target+"/screenshot"));
			Preconditions.checkState(respone.getStatusLine().getStatusCode() == 200,"Unexpected response from node");
			if (respone.getEntity()!=null){			
				try {
					allContent = EntityUtils.toString(respone.getEntity());
					respone.close();
				} catch (ParseException | IOException e) {
					LOG.error("Screenshot response improper",e);
				}		
			}		
		return allContent;
	}

	public static synchronized String getFirstSession(){
		String content = null;
		try {
			CloseableHttpResponse response = (CloseableHttpResponse) executeRequest(new HttpGet("/wd/hub/sessions"));
			Preconditions.checkState(HttpStatus.SC_NOT_FOUND != response.getStatusLine().getStatusCode(),"Node "+ getHost() + " rejects connection");
				
			content = EntityUtils.toString(response.getEntity());
			response.close();
		} catch (ParseException | IOException e) {
			LOG.error("Session response improper",e);
		}
		return content;

	}

}
