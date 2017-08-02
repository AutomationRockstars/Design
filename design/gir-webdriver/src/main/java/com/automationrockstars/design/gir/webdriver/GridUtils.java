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
package com.automationrockstars.design.gir.webdriver;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.RedirectionCalculator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


@SuppressWarnings("deprecation")
public class GridUtils {

	private static final Logger LOG = LoggerFactory.getLogger(GridUtils.class);

	public static String getNode(String gridUrl, WebDriver driver){
		String nodeUrl = gridUrl;
		CloseableHttpClient cl = HttpClients.createDefault();
		CloseableHttpResponse gridResponse = null;
		try {
			URI gridUri = new URI(gridUrl);
			URI sessionUri = new URI(
					String.format("%s://%s:%s/grid/api/testsession?session=%s", 
							gridUri.getScheme(),gridUri.getHost(),gridUri.getPort()
							,((RemoteWebDriver)DriverFactory.unwrap(driver)).getSessionId()));

			LOG.debug("Using session uri {}",sessionUri);

			gridResponse = cl.execute(new HttpPost(sessionUri));
			String response = IOUtils.toString(gridResponse.getEntity().getContent());
			LOG.debug("Response from contacting grid {}",response);

			if (gridResponse.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN){
				CloseableHttpResponse directNode = cl.execute(new HttpGet(gridUri));
				response = IOUtils.toString(directNode.getEntity().getContent());
				directNode.close();
				if (response.contains("WebDriver Hub"))
					return String.format("%s://%s:%s", gridUri.getScheme(),gridUri.getHost(),gridUri.getPort());
			}

			@SuppressWarnings("unchecked")
			Map<String,String> result = new Gson().fromJson(response, HashMap.class);
			nodeUrl = result.get("proxyId");
			
		} catch (IOException | URISyntaxException | JsonSyntaxException e) {
			try {
				URI gridUri = new URI(gridUrl);

				RequestConfig requestConfig = RequestConfig.custom()
					    .setConnectionRequestTimeout(10000)
					    .setConnectTimeout(10000)
					    .setSocketTimeout(10000)
					    .build();
				HttpGet getExtras = new HttpGet(
						String.format("%s://%s:%s/api", 
								gridUri.getScheme(),gridUri.getHost(),3000));
				getExtras.setConfig(requestConfig);
				if (200 == cl.execute(getExtras).getStatusLine().getStatusCode()){
					nodeUrl = gridUrl; 
				}

			} catch (Exception e1) {
				LOG.error("Issue with getting node from grid {}",e.getMessage());
			}

		} finally {
			try {
				if (gridResponse != null)
					gridResponse.close();
				cl.close();
			} catch (IOException e) {
			}
		}

		return nodeUrl;
	}

	public static String getNodeExtras(String gridUrl, WebDriver driver) {
		String extrasUrl = null;
		String nodeUrl = getNode(gridUrl, driver);
		
		String port = null;
		CloseableHttpClient cl = HttpClients.createDefault();
		try {
			if (nodeUrl == null){
				throw new URISyntaxException("URL: " + nodeUrl,"node url shall not be null");
			}
			URI node = new URI(nodeUrl);
			RequestConfig requestConfig = RequestConfig.custom()
				    .setConnectionRequestTimeout(10000)
				    .setConnectTimeout(10000)
				    .setSocketTimeout(10000)
				    .build();
			HttpGet portGet = new HttpGet(String.format("%s://%s:%s/extras/port", node.getScheme(),node.getHost(),node.getPort()+1));
			portGet.setConfig(requestConfig);
			CloseableHttpResponse resp = cl.execute(portGet);
			if (resp.getStatusLine().getStatusCode() == 200){
				port = IOUtils.toString(resp.getEntity().getContent());
				if(! NumberUtils.isDigits(port)){
					port = null;
				}
			} else {
				port = null;
			}
		} catch (UnsupportedOperationException | IOException e1) {
		} catch (URISyntaxException e) {
			LOG.warn("Node URI is malformed: {}",nodeUrl);
		} finally {
			try {
				cl.close();
			} catch (IOException e) {
				
			}
		}
		String extrasPort = MoreObjects.firstNonNull(port, ConfigLoader.config().getString("grid.extras.port","3000"));
		if (nodeUrl != null){
			try {
				extrasUrl = String.format("%s://%s:%s/", new URI(nodeUrl).getScheme(),new URI(nodeUrl).getHost(),extrasPort);
				if (calculator != null){
					URI extrasUri = calculator.calculate(new URI(extrasUrl));
					if (extrasUri == null){
						extrasUri = calculator.calculateByNode(new URI(nodeUrl));
					}
					if (extrasUri != null){
						extrasUrl = extrasUri.toString();
					}
				}
			} catch (URISyntaxException e) {
				LOG.debug("Issue with selenium grid extras uri",e);
			}
		}
		String confrimedUrl = null;
		try {
			cl = HttpClients.createDefault();
			RequestConfig requestConfig = RequestConfig.custom()
				    .setConnectionRequestTimeout(100000)
				    .setConnectTimeout(100000)
				    .setSocketTimeout(100000)
				    .build();
			HttpGet get = new HttpGet(extrasUrl+"/api");
			get.setConfig(requestConfig);
			HttpResponse resp = cl.execute(get);
			if (resp.getStatusLine().getStatusCode() == 200){
				confrimedUrl = extrasUrl;
			}
			
		} catch(Exception e){
			
		} finally {
			try {
				cl.close();
			} catch (Exception ignore){}
		}
		return confrimedUrl;
	}
	private static RedirectionCalculator calculator = null;
	public static synchronized void registerRedirectionCalculator(RedirectionCalculator calculator){
		GridUtils.calculator = calculator;
	}
	
	public static synchronized void verifyGridConnection(){
		if (ConfigLoader.config().getBoolean("webdriver.require.grid",false)){
			String gridUrl = ConfigLoader.config().getString("grid.url");
			if (! Strings.isNullOrEmpty(gridUrl) ){
				try {
					String host = new URI(gridUrl).getHost();
					int port = new URI(gridUrl).getPort();
					new Socket(host, port).close();
				} catch (IOException e){
					throw new AssertionError("Cannot connect to Selenium grid at "+gridUrl + " due to " + e);
				} catch (URISyntaxException e) {
						throw new AssertionError("Grid URI " + gridUrl + " is incorrect" + e);
				}
				try {
					WebDriver driver = DriverFactory.getUnwrappedDriver();
					if (Strings.isNullOrEmpty(GridUtils.getNode(gridUrl, driver))){
						throw new AssertionError("Cannot get driver from Selenium grid node");
					}
				} catch (Throwable noDriver){
					throw new AssertionError("Cannot get driver from Selenium grid due to " + noDriver);
				}				
			} else {
				throw new AssertionError("Selenium grid is not configured");
			}
		}
	}
	
}
