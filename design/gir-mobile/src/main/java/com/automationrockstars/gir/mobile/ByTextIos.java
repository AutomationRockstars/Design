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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

import io.appium.java_client.MobileBy.ByIosUIAutomation;

public class ByTextIos extends ByVisibleText {
	public ByTextIos(String... filter) {
		super(filter);
	}
	
	public List<WebElement> findElements(final SearchContext context, List<String> lines){
		List<WebElement> result = Lists.newArrayList();
		for (String line : lines){
			String search = PageUtils.getValueFromLine("path", line);
			By path = new ByIosUIAutomation("au.getElementsByIndexPaths([\""+search+"\"]);");
			WebElement part = null;
			try {
				part = path.findElement(context);
			} catch (Throwable t){
				try {
					String s = search.replaceAll("/.$", "");
				
				WebElement app = new ByIosUIAutomation("au.getElementsByIndexPaths([\""+s+"\"]);").findElement(MobileFactory.getDriver());
				part= new NotDisplayedYetVisibleUiObject(line, app);
				} catch (Exception ignore){};
			}
			if (part != null){
				result.add(part);
			}
		}
		return result;
	}
	
	public static ByTextIos iosText(String... filter){
		return new ByTextIos(filter);
	}
	
	public static ByTextIos iosTextPattern(String filter){
		ByTextIos result = iosText(filter);
		result.pattern = true;
		return result;
	}
}
