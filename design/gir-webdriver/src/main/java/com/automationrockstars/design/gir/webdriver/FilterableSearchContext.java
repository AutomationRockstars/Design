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
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class FilterableSearchContext implements SearchContext {
	public static final String STUBBORN_WAIT_PARAM = "webdriver.stubborn.wait";

	private static final ThreadLocal<Integer> wait = new InheritableThreadLocal<>();

	public static void setWait(Integer waitTime){
		wait.set(waitTime);
	}
	public static void unsetWait(){
		wait.remove();
	}
	public static boolean visibleOnly(){
		return ConfigLoader.config().getBoolean("webdriver.visibleOnly",true);

	}
	private static final Logger log = LoggerFactory.getLogger(FilterableSearchContext.class);

	private final SearchContext wrapped;
	public FilterableSearchContext(SearchContext wrapped){
		this.wrapped = wrapped;
	}


	public static boolean isVisible(WebElement element){
		try {
//			if (! element.isDisplayed()){
//				try {
//					System.out.println("SCROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOLING");
//					Page.scrollTo(element);						
//				} catch (Exception cantMove){
//					log.trace("Can't move due to",cantMove);
//				}
//			}
			boolean vis = element.isDisplayed();
			boolean click = element.isEnabled();
			Point loc  = ((Locatable)element).getCoordinates().inViewPort();
			return vis || (click && loc.getX() >0 && loc.getY()>0);
		} catch (StaleElementReferenceException e){
			return false;
		}
	}




	private static Predicate<WebElement> visible(final By by){ return new Predicate<WebElement>() {
		public boolean apply(WebElement input) {
			if (!(visibleOnly())){
				return true;
			}
			return isVisible(input);
		}

	};
	}
	private List<WebElement> findAll(SearchContext search, By by){
		List<WebElement> result = null;
		if (ConfigLoader.config().getBoolean("webdriver.cache",true)){
			result = WebCache.fromCache(search, by);
		} else { 
			unwrap(search).findElements(by);
		}
		log.trace("Found {} un-filtered elements using {} in {}",result.size(),by,search);
		return result;
	}
	public List<WebElement> findElements(By by) {
		List<WebElement> result = getVisible(by);
		if (result.isEmpty()){
			log.debug("Waiting for {} to appear",by);
			result = stubbornWait(by);
		}
		log.debug("Returning {} filtered", result);
		return result;
	}

	public WebElement findElement(By by) {
		WebElement result = Iterables.getFirst(findElements(by),null);
		if (result == null){
			throw new NoSuchElementException("Cannot find visible element identified by " + by);
		}
		return result;
	}
	public static SearchContext unwrap(SearchContext toUnwrap){
		if (WrapsDriver.class.isAssignableFrom(toUnwrap.getClass()) && WebDriver.class.isAssignableFrom(toUnwrap.getClass()))
			toUnwrap = DriverFactory.unwrap((WebDriver) toUnwrap);

		if (toUnwrap instanceof FilterableSearchContext){
			return unwrap(((FilterableSearchContext)toUnwrap).getWrapped());
		} else
			return toUnwrap;
	}
	private SearchContext unwrap(){
		return unwrap(wrapped);
	}
	private List<WebElement> getVisible(By by){
		return getVisible(unwrap(), by);
	}
	public List<WebElement> getVisible(SearchContext search, By by){
		List<WebElement> result = Lists.newArrayList(Iterables.filter(findAll(search,by),visible(by)));
		log.trace("Found {} filtered elements using {} in {}",result.size(),by,search);
		return result;
	}

	private int stubbornTime() {

		return Objects.firstNonNull(wait.get(), ConfigLoader.config().getInt(STUBBORN_WAIT_PARAM,5));
	}
	private List<WebElement> stubbornWait(final By by){
		List<WebElement> result = Lists.newArrayList();
		try { result = Waits.webElementWait(unwrap())
				.withTimeout(stubbornTime(), TimeUnit.SECONDS)
				.pollingEvery(WebDriverWait.DEFAULT_SLEEP_TIMEOUT,TimeUnit.MILLISECONDS)
				.until(new Function<SearchContext, List<WebElement>>() {
					@Override
					public List<WebElement> apply(SearchContext input) {
						try {
							List<WebElement> result = getVisible(input, by);
							if (result.isEmpty()) return null;
							return result;
						} catch (NoSuchElementException e) {
							return null;
						}
					}
				});

		return result;
		} catch (TimeoutException e){
			return result;
		}
	}

	public String toString(){
		return String.format("Filterable of %s", wrapped);
	}
	private SearchContext getWrapped(){
		return wrapped;
	}


}
