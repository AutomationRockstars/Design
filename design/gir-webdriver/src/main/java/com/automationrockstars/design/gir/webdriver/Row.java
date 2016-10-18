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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Row extends UiObject{

	public Row(WebElement toWrap){
		super(toWrap);
	}
	
	public List<WebElement> cells(){
		return this.findElements(By.tagName("td"));
	}
	public WebElement cell(int i){
		return this.findElement(By.xpath(".//td["+i+"]"));
	}
	public boolean hasText(final String textToFind){
		return this.getText().contains(textToFind);
	}
}
