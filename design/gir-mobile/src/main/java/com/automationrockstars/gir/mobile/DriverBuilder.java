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
package com.automationrockstars.gir.mobile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.design.gir.webdriver.GridUtils;

import com.google.common.base.Strings;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class DriverBuilder {
	DesiredCapabilities capabilities = new DesiredCapabilities();

	private static Logger LOG = LoggerFactory.getLogger(DriverBuilder.class);


	public DriverBuilder() {
		capabilities.setCapability("newCommandTimeout", ConfigLoader.config().getInt("appium.command.timeout",600));
		capabilities.setCapability("deviceReadyTimeout", ConfigLoader.config().getInt("appium.device.timeout",600));
		capabilities.setCapability("sendKeyStrategy", "groupped");
	}

	private void initialRun(URL grid){
		MobileFactory.resetDriver();
		capabilities.setCapability("autoAcceptAlerts",true);
		LOG.info("Using capabilities {}", capabilities);
		if (! android){
			MobileFactory.driver = new IOSDriver<>(grid, capabilities);
		} else {
			MobileFactory.driver = new AndroidDriver<>(grid, capabilities);	
		}
		LOG.info("Initial run succesfull");
	}
	
	public AppiumDriver build(int timeoutInSeconds){
		int poll = ConfigLoader.config().getInt("mobile.driver.create.poll",10);
		return new FluentWait<Object>(new Object())
				.withMessage("Cannot get phone driver")
				.withTimeout(timeoutInSeconds, TimeUnit.SECONDS)
				.ignoring(Throwable.class)
				.pollingEvery(poll, TimeUnit.SECONDS)
				.until(new Function<Object,AppiumDriver>() {

			@Override
			public AppiumDriver apply(Object input) {
				return doBuild();				 
			}
		});
	}

	public AppiumDriver build(){
		return doBuild();
	}
	private AppiumDriver doBuild(){

		String proxyUrl = ConfigLoader.config().getString("proxy.url");
		if (! Strings.isNullOrEmpty(proxyUrl)){
			Proxy proxy = new Proxy()
			.setHttpProxy(proxyUrl)
			.setSslProxy(proxyUrl);
			capabilities.setCapability(CapabilityType.PROXY, proxy);
		}
		if (acceptAlerts){
			capabilities.setCapability("autoAcceptAlerts",true);
		}
		URL grid = null;
		try {
			grid = new URL(ConfigLoader.config().getString("grid.url"));
			LOG.info("Using grid {}",grid);
		} catch (MalformedURLException e) {
			LOG.error("Grid URL not provided or wrong {}",ConfigLoader.config().getString("grid.url"),e);
		}
		try {
			if (android){
				capabilities = DesiredCapabilities.android().merge(capabilities);
				capabilities.setCapability("deviceName",ConfigLoader.config().getString("mobile.device","android phone"));
				MobileFactory.driver =  new AndroidDriver<>(grid,capabilities);

			} else {
				capabilities = DesiredCapabilities.iphone().merge(capabilities);
				capabilities.setCapability("deviceName",ConfigLoader.config().getString("mobile.device","iPhone 6"));
				try {
					MobileFactory.driver = new IOSDriver<>(grid, capabilities);
				} catch (SessionNotCreatedException e){
					if (e.getMessage().contains("App did not have elements") ||
							e.getMessage().contains("crashed")){
						LOG.info("Issue creating driver due to initialization: {}",e.getMessage());
						initialRun(grid);
						MobileFactory.resetDriver();
						new DriverBuilder().build();
					} else {
						throw e;
					}
				}
			} 

		}catch (Exception e){
			MobileFactory.driver = null;
			if (e.getMessage().contains("Request timed out waiting for a node ")){
				LOG.error("Node not available due to ",e);
				throw new RuntimeException("Cannot get free node", e);
			} else {
				LOG.error("Cannot create new driver due to ",e);
				throw e;
			}
			

		}

		LOG.info("Driver instance created {}",MobileFactory.driver);
		LOG.info("Test session {}",MobileFactory.driver.getSessionId());
		System.setProperty(DriverFactory.WEBDRIVER_SESSION, MobileFactory.driver.getSessionId().toString());
		try {
			ConfigLoader.config().addProperty("webdriver.video", String.format("%s/download_video/%s.mp4", GridUtils.getNodeExtras(grid.toString(), MobileFactory.driver), MobileFactory.driver.getSessionId().toString()));
			LOG.info("Executing on node {}",GridUtils.getNode(grid.toString(), MobileFactory.driver));
			checkGridExtras(grid.toString());
		}catch (Exception e) {
			LOG.error("No grid extras found", e);
		}
		return MobileFactory.driver;
	}

	private static void checkGridExtras(String gridUrl){
		String gridExtras = GridUtils.getNodeExtras(gridUrl, MobileFactory.driver);
		if (gridExtras == null){
			LOG.info("No grid extras foud");
		} else {
			LOG.info("Grid extras available at {}",gridExtras);
		}
	}
	public DriverBuilder forApp(String appFile){
		capabilities.setCapability("app",appFile);
		return this;
	}

	public DriverBuilder dontReset(){
		capabilities.setCapability("noReset", true);
		return this;
	}

	public DriverBuilder fullReset(){
		capabilities.setCapability("fullReset", true);
		return this;
	}
	public DriverBuilder forPackage(String appPackage){
		capabilities.setCapability("appPackage", appPackage);
		return this;
	}

	public DriverBuilder forActivity(String appActivity){
		capabilities.setCapability("appActivity", appActivity);
		return this;
	}
	private boolean android = false;

	public DriverBuilder android(){
		android = true;
		return this;
	}
	private boolean acceptAlerts = false;
	public DriverBuilder acceptAlerts(){
		this.acceptAlerts = true;
		return this;
	}

	public DriverBuilder iOS(){
		android = false;
		return this;
	}



}
