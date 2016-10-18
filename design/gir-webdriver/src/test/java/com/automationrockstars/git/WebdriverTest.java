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
package com.automationrockstars.git;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;

public class WebdriverTest {

	private static final Logger LOG = LoggerFactory.getLogger(WebdriverTest.class);


	@Test
	public void test() throws InterruptedException, IOException {
		if (ConfigLoader.config().containsKey("noui")){
			int res = -1;
			try {
				res = new ProcessBuilder("phantomjs").start().waitFor(); 
			} catch (Exception e){
				res = -1;
			}
			if (res == 0){
				ConfigLoader.config().setProperty("webdriver.browser", "phantomjs");
			} else {
				return ;
			}
		}
		Ebay.searchFor("aa");
	}

}
