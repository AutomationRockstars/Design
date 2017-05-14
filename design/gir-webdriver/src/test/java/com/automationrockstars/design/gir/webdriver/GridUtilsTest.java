/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.design.gir.webdriver;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.automationrockstars.bmo.RedirectionCalculator;

public class GridUtilsTest {

//	@Test
//	@Ignore
	public void test() {
		String gridUrl = "http://10.64.103.231:4444/wd/hub/";
		System.setProperty("grid.url", gridUrl);
		System.setProperty("url", "www.google.ie");
		WebDriver driver = DriverFactory.getDriver();
		try {
			GridUtils.registerRedirectionCalculator(new RedirectionCalculator() {
				
				@Override
				public URI calculate(URI original) {
					return URI.create(String.format("http://%s:30303030003", original.getHost()));
				}

				@Override
				public URI calculateByNode(URI nodeUri) {
					// TODO Auto-generated method stub
					return null;
				}
			});
			System.out.println("UOLABOGA");
			System.out.println(GridUtils.getNode(gridUrl, DriverFactory.getUnwrappedDriver()));
			System.out.println(GridUtils.getNodeExtras(gridUrl, DriverFactory.getUnwrappedDriver()));
		} catch (Throwable t){
			t.printStackTrace();
		}
		driver.close();
		driver.quit();
	}

}
