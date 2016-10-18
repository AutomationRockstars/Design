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
package com.automationrockstars.bmo.console.web;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.google.common.io.Resources;

public class WebIdeHandler implements HttpRequestHandler{

	public static final String INDEX = "web/index.html"; 
	public static final String BASE = "web/app/";
	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		
		final String uri = request.getRequestLine().getUri(); 
		if (! uri.contains("app")){
			response.setEntity(new FileEntity(new File(Resources.getResource(INDEX).getFile())));
		} else {
			String fileName = uri.split("app/")[1];
			response.setEntity(new FileEntity(new File(Resources.getResource(BASE + fileName).getFile())));
		}
		
	}

}
