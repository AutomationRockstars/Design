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
package com.automationrockstars.design.gir.webdriver.plugin;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.internal.Coordinates;

import com.automationrockstars.design.gir.webdriver.UiObject;

public interface UiObjectInfoPlugin {

	void beforeGetTagName(UiObject element);
	void afterGetTagName(UiObject element, String value);

	void beforeGetAttribute(UiObject element,String name);
	void afterGetAttribute(UiObject element,String name, String value);

	void beforeIsSelected(UiObject element);
	void afterIsSelected(UiObject element, boolean value);

	void beforeIsEnabled(UiObject element);
	void afterIsEnabled(UiObject element, boolean value);

	void beforeGetText(UiObject element);
	void afterGetText(UiObject element, String value);
	
	void beforeIsDisplayed(UiObject element);
	void afterIsDisplayed(UiObject element, boolean value);

	void beforeGetLocation(UiObject element);
	void afterGetLocation(UiObject element, Point value);

	void beforeGetSize(UiObject element);
	void afterGetSize(UiObject element, Dimension value);

	void beforeGetCssValue(UiObject element, String propertyName);
	void afterGetCssValue(UiObject element, String propertyName, String value);
	
	<X> void beforeGetScreenshotAs(UiObject element,OutputType<X> target);
	<X> void afterGetScreenshotAs(UiObject element, OutputType<X> target, X screenshot);
	
	void beforeGetCoordinates(UiObject uiObject);
	void afterGetCoordinates(UiObject uiObject, Coordinates result);
	
	void beforeGetRect(UiObject uiObject);
	void afterGetRect(UiObject uiObject,Rectangle rect);
}
