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

import static com.automationrockstars.design.gir.webdriver.DriverFactory.getUnwrappedDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementClassAnnotationsHandler;

public class Waits {

	public static void waitUntilVisible(UiObject element){
		waitUntilVisible(element.getWrappedElement());
	}
	
	
	
	public static WebElement waitUntilVisible(By by){
		return DriverFactory.delay().until(findVisible(by));
	}
	private static Function<SearchContext,Boolean> isNotMoving(final By by){
	return new Function<SearchContext,Boolean>(){
		Point previous = new Point(-7, -77);
		public String toString(){
			return String.format("Element identified by %s is not moving", by);
		}
		@Override
		public Boolean apply(SearchContext input) {
			Point current = webElementWait(input).until(visible(by)).getLocation();
			if (previous.equals(current)){
				return true;
			} else {
				previous = current;
				return false;
			}
		}
	};}
	
	public static void waitUntilNotMoving(final WebElement element){
		withDefaultDelay().until(new Function<WebDriver,Boolean>() {
			Point previous = element.getLocation();
			@Override
			public Boolean apply(WebDriver input) {
				if (element.getLocation().equals(previous)){
					return true;
				} else {
					previous = element.getLocation();
					return false;
				}
			}
		});
	}
	public static void waitUntilNotMoving(final By uiElement){
		webElementWait(getUnwrappedDriver()).until(isNotMoving(uiElement));
	}
	private static ExpectedCondition<WebElement> findVisible(final By by){
		return new ExpectedCondition<WebElement>() {

			@Override
			public WebElement apply(WebDriver input) {
				if (input instanceof FilterableSearchContext){
					try {
						return input.findElement(by);
					} catch (Throwable e){
						return null;
					}
				} else {
					try {
						return DriverFactory.getDriver().findElement(by);
					} catch (Throwable e){
						return null;
					}
				}
			}
			
			@Override
			public String toString(){
				return String.format("Visibility of element found %s", by);
			}
		};

	}
	public static WebElement waitUntilVisible(WebElement element){
		return DriverFactory.delay().until(visibilityOf(element));
	}


	public static WebElement waitUntilClickable(WebElement element){
		return DriverFactory.delay().until(elementToBeClickable(element));
	}


	public static void waitUntilHidden(By by){
		DriverFactory.delay().until(invisibilityOfElementLocated(by));
	}

	public static void waitUntilHidden(UiObject element){
		waitUntilHidden(element.getLocator());
	}

	@SuppressWarnings("unchecked")
	public static void waitUntilHidden( HtmlElement element){
		By by = null;
		try {
			by= new HtmlElementClassAnnotationsHandler<HtmlElement>((Class<HtmlElement>) element.getClass()).buildBy();
		} catch (Throwable ignore){
			if (element instanceof UiObject){
				by = ((UiObject) element).getLocator();
			}
		}
		if (by != null){
			waitUntilHidden(by);
		}
	}

	public static ExpectedCondition<UiObject> anyVisible(final By...bys){
		return new ExpectedCondition<UiObject>() {
			@Override
			public UiObject apply(WebDriver input) {
				while (input.getClass().isAssignableFrom(WrapsDriver.class) ){
					input = ((WrapsDriver)input).getWrappedDriver();
				}
				int wait = ConfigLoader.config().getInt(FilterableSearchContext.STUBBORN_WAIT_PARAM,5);
				ConfigLoader.config().setProperty(FilterableSearchContext.STUBBORN_WAIT_PARAM, 0);
				UiObject result = null;
				SearchContext ctx = new FilterableSearchContext(input);
				for (By by : bys){
					try{
						result = new UiObject(ctx.findElement(by),by);
						if (result.isDisplayed()){
							break;
						} else {
							result = null;
						}
						
					} catch (Exception ignore){
					} 
				}
				ConfigLoader.config().setProperty(FilterableSearchContext.STUBBORN_WAIT_PARAM, wait);
				return result;
			}
			public String toString(){
				return String.format("Visiblity of any of elements %s", Arrays.toString(bys));
			}
			
		};
	}
	public static UiObject waitForAnyVisible(final By... bys){
		return DriverFactory.delay().until(anyVisible(bys));
	
	}

	public static void waitUntilSourceContains(final String pageSourcePart){
		DriverFactory.delay().until(new Function<WebDriver,Boolean>() {

			@Override
			public Boolean apply(WebDriver input) {
				return DriverFactory.getDriver().getPageSource().contains(pageSourcePart);
			}

			public String toString(){
				return String.format("Page source contains %s", pageSourcePart);
			}
		});
	}

	public static WebDriverWait withDefaultDelay(){
		return DriverFactory.delay();	
	}

	public static WebDriverWait withDelay(int seconds){
		return DriverFactory.delay(seconds);
	}



	public static ExpectedCondition<WebElement> clickable(By by){
		return elementToBeClickable(by);
	}
	
	public static FluentWait<SearchContext> webElementWait(SearchContext context){
		return new FluentWait<SearchContext>(context);
	}
	
	public static Function<SearchContext,WebElement> visible(final By by){
		return new Function<SearchContext, WebElement>() {

			@Override
			public WebElement apply(SearchContext input) {
				Optional<WebElement> visibleElement = Iterables.tryFind(FilterableSearchContext.unwrap(input).findElements(by), new Predicate<WebElement>() {
					@Override
					public boolean apply(WebElement input) {
						return input.isDisplayed();
					}
				});
				
				if (visibleElement.isPresent()){
					return visibleElement.get();
				} else {
					return null;
				}}};}
	public static Function<SearchContext,Boolean> hidden(final By by){
		return new Function<SearchContext,Boolean>() {

			@Override
			public Boolean apply(SearchContext input) {
				Optional<WebElement> visibleElement = Iterables.tryFind(FilterableSearchContext.unwrap(input).findElements(by), new Predicate<WebElement>() {
					@Override
					public boolean apply(WebElement input) {
						return input != null && input.isDisplayed();
					}
				});
				return ! visibleElement.isPresent();
			}
		};}
 
}

