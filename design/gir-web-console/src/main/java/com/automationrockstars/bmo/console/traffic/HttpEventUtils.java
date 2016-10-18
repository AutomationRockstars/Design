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
package com.automationrockstars.bmo.console.traffic;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class HttpEventUtils {

	private static final Logger LOG = LoggerFactory.getLogger(HttpEventUtils.class);

	public static HttpRequestEvent create(HttpRequest toClone){
		HttpRequestEvent event = new HttpRequestEvent();
		event.setHeaders(toClone.getAllHeaders());
		event.setRequestLine(toClone.getRequestLine());
		if (HttpEntityEnclosingRequest.class.isAssignableFrom(toClone.getClass()) ){	
			HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) toClone;
			HttpEntity entity = request.getEntity();
			event.setContent(getContentFromEntity(entity));			
		}
		return event;
	}
	private static HttpEntity getWrapped(HttpEntity wrapping){
		if (wrapping instanceof HttpEntityWrapper){
			try {
				Field wrapped = HttpEntityWrapper.class.getDeclaredField("wrappedEntity");
				wrapped.setAccessible(true);
				HttpEntity result = (HttpEntity) wrapped.get(wrapping);
				return result;
			} catch (Exception e) {
				LOG.warn("Cannot unwrap {} due to",wrapping,e);
			}
		}
		return wrapping;
	}
	public static HttpResponseEvent create(HttpResponse response){
		HttpResponseEvent result = new HttpResponseEvent();
		result.setStatusLine(response.getStatusLine());
		result.setHeaders(response.getAllHeaders());
		result.setContent(getContentFromEntity(getWrapped(response.getEntity())));
		return result;
	}
	
	private static BufferedInputStream getContentFromEntity(HttpEntity entity){
		BufferedInputStream wrapped = null;
		try {

			Optional<Method> setContent = Iterables.tryFind(Lists.newArrayList(entity.getClass().getMethods()), new Predicate<Method>() {
				@Override
				public boolean apply(Method input) {
					return "setContent".equals(input.getName());
				}
			});
			if (setContent.isPresent()){
				InputStream s = entity.getContent();
				wrapped = new BufferedInputStream(s);
			
				setContent.get().invoke(entity, wrapped);
			} else if (entity.isRepeatable()){
				wrapped = new BufferedInputStream(entity.getContent());
			} else {
				LOG.warn("Content of request cannot be safely fetched");
			}

		} catch (Exception e) {
			LOG.warn("Copying content of request failed",e);
		}
		return wrapped;
	}
	
	public static void removeHeaders(HttpRequest request, String... headers){
		List<Header> allHeaders = Lists.newArrayList(request.getAllHeaders());
		List<Header> headersToRemove = Lists.newArrayList();
		for (final Header header : allHeaders){
			if (Iterables.tryFind(Lists.newArrayList(headers), new Predicate<String>() {

				@Override
				public boolean apply(String input) {
					return header.getName().equalsIgnoreCase(input);
				}
			}).isPresent()){
				headersToRemove.add(header);
			}
		}
		for (Header header : headersToRemove){
			request.removeHeader(header);
		}
	}
	
	public static HttpRequest create(HttpRequestEvent request){
		HttpRequest result = null;
		if (request.getContent() != null){
			BasicHttpEntity e = new BasicHttpEntity();
			e.setContent(request.getContent());
			result = new BasicHttpEntityEnclosingRequest(request.getRequestLine());
		} else {
			result = new BasicHttpRequest(request.getRequestLine());
		}
		result.setHeaders(request.getHeaders().toArray(new Header[1]));
		return result;		
	}
	public static HttpResponse create(HttpResponseEvent httpResponseEvent) {
		BasicHttpResponse result = new BasicHttpResponse(httpResponseEvent.getStatusLine());
		if (httpResponseEvent.readContent() != null){
			BasicHttpEntity e = new BasicHttpEntity();
			e.setContent(new ByteArrayInputStream(httpResponseEvent.readContent()));
			result.setEntity(e);
		}
		result.setHeaders(httpResponseEvent.getHeaders().toArray(new Header[1]));
		return result;
	}
	
	public static byte[] copyToBytes(BufferedInputStream in){
		byte[] result = null;
		if (in != null){			
			in.mark(1024000000);
			try {
				LOG.info("There is {} data ",in.available());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				result = IOUtils.toByteArray(in);
				in.reset();
			} catch (IOException e) {
				LOG.error("Reading stream failed {}",e.getMessage());
			}
		}
		 return result;
	}
	public static BufferedInputStream copy(BufferedInputStream in){
		return  new BufferedInputStream( new ByteArrayInputStream(copyToBytes(in)));
	}
}



