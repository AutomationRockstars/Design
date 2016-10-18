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

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
public class MobileSearchUtils {

	private static Logger LOG = LoggerFactory.getLogger(MobileSearchUtils.class);

	public static MobileUiObject findVisible(By by){
		if (by instanceof ByVisibleTextIos){
			((ByVisibleTextIos) by).addVisible();
		}
		WebElement plain = MobileFactory.getDriver().findElement(by);
		return new MobileUiObject(plain, by);
	}
	
	@SuppressWarnings("unchecked")
	public static List<MobileUiObject> findAllVisible(final By by){
		if (by instanceof ByVisibleTextIos){
			((ByVisibleTextIos) by).addVisible();
		}
		return Lists.newArrayList(Iterables.transform(MobileFactory.getDriver().findElements(by), new Function<WebElement, MobileUiObject>() {
			@Override
			public MobileUiObject apply(WebElement input) {
				return MobileUiObjectFactory.from(input,by);
			}
			
		}));
	}

	public static UiObject findPresent(By by){

		WebElement plain = by.findElement(MobileFactory.getDriver());
		return new UiObject(plain, by);
	}
	public static UiObject waitForVisible(final By by){
		try {
			WebElement plain = MobileFactory.delay().until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver input) {
					try{
						return findVisible(by);
					} catch (Exception e){
						return null;
					}
				}
				
				public String toString(){
					return String.format("visibility of %s", by);
				}
			});
			return new UiObject(plain, by);
		} catch (Exception e){
			LOG.error("Element {} not found",by,e);
			LOG.trace(PageUtils.getPageSource());
			throw new NoSuchElementException(String.format("Element identified by %s not visible",by));
		}
	}

	public static UiObject waitForPresent(By by){
		try {
			WebElement plain = MobileFactory.delay().until(presenceOfElementLocated(by));
			return new UiObject(plain, by);
		} catch (Exception e){
			LOG.error("Element {} not found",by,e);
			LOG.trace(PageUtils.getPageSource());
			throw new NoSuchElementException(String.format("Element identified by %s not visible",by));

		}

	}


	public static boolean isPresent(By by){
		try {
			return MobileFactory.getDriver().findElement(by) != null;
		} catch (Exception e){
			return false;
		}

	}

	public static boolean isVisible(By by){
		try {
			return findVisible(by) != null;
		} catch (Exception e){
			return false;
		}
	}

	public static ExpectedCondition<UiObject> anyPresent(final By... bys){
		return new ExpectedCondition<UiObject>() {
			@Override
			public UiObject apply(WebDriver input) {
				UiObject result = null;
				for (By by : bys){
					try {
						result = new UiObject(MobileFactory.getDriver().findElement(by),by);
						break;
					} catch (Exception e){

					}
				}
				return result;
			}
		};
	}
	public static UiObject waitForAnyPresent(final By... bys){
		return MobileFactory.delay().until(anyPresent(bys));
	}

	public static ExpectedCondition<UiObject> anyVisible(final By... bys){
		return new ExpectedCondition<UiObject>() {
			@Override
			public UiObject apply(WebDriver input) {
				UiObject result = null;
				for (By by : bys){
					try {
						if (by instanceof ByVisibleTextIos){
							((ByVisibleTextIos)by).addVisible(); 
						}
						result = new UiObject(MobileFactory.getDriver().findElement(by),by);
						break;
					} catch (Exception e){
					}
				}
				return result;
			}
			
			@Override
			public String toString(){
				return String.format("visibility of any %s", Arrays.toString(bys));
			}
		};
	}


	public static UiObject waitForAnyVisible(final By... bys){
		return MobileFactory.delay().until(anyVisible(bys));

	}

}
