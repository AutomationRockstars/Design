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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdriverUtils {

	private static final Logger log = LoggerFactory.getLogger(WebdriverUtils.class);
	
	public static List<WebElement> getChildren(WebElement parent){
		log.debug("Looking for children of {}", webElementToString(parent));
		List<WebElement> children = parent.findElements(By.xpath(".//*"));
		for (WebElement child : children){
			log.debug("Found {} ",webElementToString(child));
	
		}
		return children;
	}
	
	public static String webElementToString(WebElement element){
		StringBuilder result = new StringBuilder();
		try {
			result.append(element.getText()).append(" ");
			result.append(element.getTagName()).append(" ");
			result.append(element.toString());
		} catch (Throwable ignore){
			log.info("Strange stuff",ignore);
		}
		return result.toString();
	}
}
