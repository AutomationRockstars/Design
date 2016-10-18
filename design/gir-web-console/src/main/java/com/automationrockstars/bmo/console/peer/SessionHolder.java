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

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.bmo.console.traffic.ConsoleEventBus;
import com.automationrockstars.bmo.console.traffic.HttpEventUtils;
import com.automationrockstars.bmo.console.traffic.HttpRequestEvent;
import com.automationrockstars.bmo.console.traffic.HttpResponseEvent;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Atomics;
import com.google.gson.Gson;

public class SessionHolder {

	private static final ConcurrentMap<String, HttpResponseEvent> sessions = Maps.newConcurrentMap();
	private static final ConcurrentMap<String, HttpRequestEvent> deleteSessions = Maps.newConcurrentMap();

	public static final String DEBUG_SESSION_KEY = "debugSessionId";

	private static boolean isNewSessionRequest(HttpRequest request){
		return ("POST".equalsIgnoreCase(request.getRequestLine().getMethod()))
				&& ("/wd/hub/session").equalsIgnoreCase(request.getRequestLine().getUri());
	}
	private static final Logger LOG = LoggerFactory.getLogger(SessionHolder.class);
	private static boolean containsSessionId(HttpRequestEvent request){
		String content = new String(request.readContent()); 
		LOG.info("Check if debugSessionId exists " + content);
		return content.contains(DEBUG_SESSION_KEY);
	}
	private static String getDebugSessionId(HttpRequestEvent request){
		byte[] content =  request.readContent();
		String json = new String(content);
		LOG.info("Fetch debugSessionId " + json);
		return json.split(DEBUG_SESSION_KEY+"\":")[1].split("\"")[1];
	}

	private static boolean hasSession(String debugSessionId){
		return sessions.containsKey(debugSessionId);
	}
	private static boolean isDeleteSessionRequest(HttpRequest request){
		return "DELETE".equalsIgnoreCase(request.getRequestLine().getMethod());
	}

	private static ThreadLocal<Boolean> saveResponse = new ThreadLocal<Boolean>(){
		@Override
		protected Boolean initialValue(){
			return Boolean.FALSE;
		}
	};

	private static ThreadLocal<String> currentDebugSessionId = new ThreadLocal<>();
	@Subscribe
	public void filterRequest(HttpRequest request){
		HttpRequestEvent requestEvent = HttpEventUtils.create(request);
		ConsoleEventBus.readOnly.post(requestEvent);
		if (isNewSessionRequest(request) && containsSessionId(requestEvent)){
			String debugSessionId = getDebugSessionId(requestEvent);
			currentDebugSessionId.set(debugSessionId);
			if (hasSession(debugSessionId)){
				ConsoleEventBus.readOnly.post(sessions.get(debugSessionId));
				ConsoleEventBus.execution.post(HttpEventUtils.create(sessions.get(debugSessionId)));
			} else {
				saveResponse.set(true);
				ConsoleEventBus.execution.post(request);
			} 
		}else if (isDeleteSessionRequest(request) && currentDebugSessionId.get() != null){
			deleteSessions.putIfAbsent(currentDebugSessionId.get(), requestEvent);
			ConsoleEventBus.execution.post(fakeDeleteResponse());
		} else {
			ConsoleEventBus.execution.post(request);
		}
	}
	private static boolean isNewSessionResponse(HttpResponseEvent response){	
		String content = new String(response.readContent());
		return content.contains("sessionId\":");
	}
	@Subscribe
	public void  filterResponse(HttpResponse response){
		HttpResponseEvent responseEvent = HttpEventUtils.create(response);
		ConsoleEventBus.readOnly.post(responseEvent);
		if (saveResponse.get() && isNewSessionResponse(responseEvent)){
			sessions.put(currentDebugSessionId.get(), responseEvent);
			saveResponse.set(false);
		}
		ConsoleEventBus.execution.post(response);
	}

	private static Gson parser = new Gson();

	private static String getCurrentSessionId(){
		Map<String,String> result = parser.fromJson(new String(sessions.get(currentDebugSessionId.get()).readContent()), HashMap.class);
		LOG.info("current " + result + currentDebugSessionId.get() + sessions);
		target.set(result.get("sessionId"));
		return result.get("sessionId");

	}

	private static AtomicReference<String> target = Atomics.newReference();

	private static String getTempTarget(){
		String result = RequestToNode.getFirstSession();
		Map<String,Object> sessionId = parser.fromJson(result, HashMap.class);
		if ( sessionId.get("value")!=null){
			result = (String) ((Map<String, Object>) ((List)sessionId.get("value")).get(0)).get("id"); 			
		} else {				
			result = null;
		}
		return result;
	}
	public static String getCurrentTarget(){
		String result = target.get();
		if (Strings.isNullOrEmpty(result)){
			result = getTempTarget();
		}
		return result;
	}
	private static HttpResponse fakeDeleteResponse(){
		BasicHttpResponse result = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("HTTP", 1,1), HttpStatus.SC_OK, "OK")); 
		BasicHttpEntity ent = new BasicHttpEntity();
		ent.setContent(new ByteArrayInputStream(String.format(
				"{\"sessionId\":\"%s\",\"status\":0,\"value\":null,\"state\":\"success\",\"class\":\"org.openqa.selenium.remote.Response\",\"hCode\":364401645}", 
				getCurrentSessionId()).getBytes()));
		result.setEntity(ent);
		return result;
	}

	public static void closeSessions(){
		for (Entry<String, HttpRequestEvent> delete : deleteSessions.entrySet()){
			ConsoleEventBus.execution.post(HttpEventUtils.create(delete.getValue()));
		}
		sessions.clear();
		deleteSessions.clear();
	}
}
