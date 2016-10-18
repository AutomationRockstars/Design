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

import java.lang.reflect.Field;

import org.jboss.netty.util.internal.ThreadLocalBoolean;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.exceptions.HtmlElementsException;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementClassAnnotationsHandler;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementFieldAnnotationsHandler;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

public abstract class UiFragment extends HtmlElement implements HasLocator{


	public UiFragment(){
		this(DriverFactory.getDriver());

	}


	private static ThreadLocal<Boolean> loaded = new ThreadLocalBoolean(false);

	public UiFragment(SearchContext searchContext){
		super();
		
		By classBy = null;
		if (this.getClass().getAnnotation(InitialPage.class) != null){
			if (! loaded.get()){
				String url = ConfigLoader.config().getString("url",this.getClass().getAnnotation(InitialPage.class).url());
				DriverFactory.getDriver().get(url);
				if (DriverFactory.getDriver().getTitle().contains("Certificate Error")){
					DriverFactory.getDriver().findElement(By.name("overridelink")).click();
				}
				loaded.set(true);
			}
			
			if (this.getClass().getAnnotation(FindBy.class) == null && this.getClass().getAnnotation(FindBys.class) == null){
				classBy = By.tagName("body");
			}
		}
		PageFactory.initElements(new HtmlElementDecorator(new HtmlElementLocatorFactory(searchContext)), this);
		if(super.getWrappedElement() == null || ((! super.getWrappedElement().isDisplayed()) && FilterableSearchContext.visibleOnly())){
			try {
				if (classBy == null){
					classBy = new HtmlElementClassAnnotationsHandler<>(this.getClass()).buildBy();
				}
				try {
					WebElement plain = Waits.webElementWait(searchContext).until(Waits.visible(classBy));
					super.setWrappedElement(plain);
				} catch (TimeoutException e){
					WebElement plain = searchContext.findElement(classBy);
					super.setWrappedElement(plain);
				}
			} catch (HtmlElementsException missingAnnotation){
				throw new RuntimeException("Verify if @FindBy annotation is present on class",missingAnnotation);
			}
		}
		if (super.getName() == null && this.getClass().isAnnotationPresent(Name.class)){
			super.setName(this.getClass().getAnnotation(Name.class).value());
		}
	}

	public By getLocator(){
		return new HtmlElementClassAnnotationsHandler<>(this.getClass()).buildBy();
	}

	public static By getLocator(Class<? extends UiFragment> clazz){
		return new HtmlElementClassAnnotationsHandler<>(clazz).buildBy();
	}

	public static By getLocator(Field uiObject){
		return new HtmlElementFieldAnnotationsHandler(uiObject).buildBy();
	}

	public By getLocator(String fieldName){
		try {
			return getLocator(this.getClass().getField(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new WebDriverException(String.format("UiFragment %s does not have field %s declared", this.getClass(),fieldName));
		}
	}
	public boolean has(final By by){
		return Waits.visible(by).apply(FilterableSearchContext.unwrap(this))!= null;
	}

	public boolean hasText(final String text){
		return this.getText().contains(text);
	}

	public static boolean isPresent(Class<? extends UiFragment> fragment){
		By classBy = new HtmlElementClassAnnotationsHandler<>(fragment).buildBy();
		if (Page.isElementPresent(classBy) ){
			return ! new FilterableSearchContext(DriverFactory.getUnwrappedDriver()).findElements(classBy).isEmpty();
		} else return false;
	}

	public static boolean isVisible(Class<? extends UiFragment> fragment){
		try {
			return Page.isElementVisible(getLocator(fragment));
		} catch (WebDriverException e){
			return false;
		}
	}

	public static void waitForHidden(final Class<? extends UiFragment> uiFragment, int timeout){
		Waits
		.withDelay(timeout)
		.until(new Predicate<WebDriver>() {
			@Override
			public boolean apply(WebDriver input) {
				return ! isVisible(uiFragment);
			}
		});
	}

	public static void waitForHidden(Class<? extends UiFragment> uiFragment){
		waitForHidden(uiFragment, ConfigLoader.config().getInt("hiding.timeout",120));
	}
	
	public FluentIterable<UiObject> children(){

		return FluentIterable.from(this.findElements(By.xpath(".//*"))).transform(new Function<WebElement,UiObject>(){
			int instance = 0;
			@Override
			public UiObject apply(WebElement input) {
				return UiObject.wrap(input, By.xpath(".//*["+(instance++) +"]"));
			}});
	}
}
