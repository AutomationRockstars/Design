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
package com.automationrockstars.bmo;

import org.openqa.selenium.WebDriver;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Preconditions;

public class WebdriverJunitGroovyRunner extends JunitGroovyRunner{
	public static final String URL_PROP = "url";
	private static WebDriver driver ;
	
	protected synchronized void prepBinding(){
		driver = DriverFactory.getDriver();
		
		driver.manage().window().maximize();
		binding.setVariable("driver", driver);
		}
	
	public void executeScript() throws Exception{
		String url = System.getProperty(URL_PROP);
		Preconditions.checkNotNull(url,"System property %s is not set. Please use -D%s=http://link to execute tests", URL_PROP);
		try {
			prepBinding();
			driver.get(url);
			super.executeScript();
		} finally {
			driver.close();
		}
	}
}
