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

public class ByVisibleTextAndroid extends ByVisibleText {

	
	public ByVisibleTextAndroid(String...filter) {
		super(filter);
	}

	public List<WebElement> findElements(final SearchContext context, final List<String> lines){
		List<WebElement> result = Lists.newArrayList();
		for (String line : lines){
			WebElement part;
			if (line.contains("content-desc")&& PageUtils.getValueFromLine("content-desc", line).length() > 0){
				part= By.name(PageUtils.getValueFromLine("content-desc", line)).findElement(context);			
			} else {
				String className = line.replaceAll("^.*<", "").replaceAll("\\ .*", "");
				String instance = PageUtils.getValueFromLine("instance", line);			
				part = By.xpath(String.format("//%s",className)).findElements(context).get(Integer.valueOf(instance));
			}
			result.add(part);
		}
		return result;
	}
}
