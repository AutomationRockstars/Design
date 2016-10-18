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

import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class UiObjectFactory {
	
	private static Logger LOG = LoggerFactory.getLogger(UiObjectFactory.class);
	public static UiObject from(By by){
		UiObject result =  new UiObject(by);
		result.getWrappedElement();
		return result;
	}


	public static <T> T decorate(UiObject uiObject, Class<T> klass){
		try {
			return klass.getConstructor(WebElement.class).newInstance(uiObject);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOG.error("Cannot decorate {} with class {}",uiObject,klass);
			Throwables.propagate(e);
		}
		return null;
	}
 }
