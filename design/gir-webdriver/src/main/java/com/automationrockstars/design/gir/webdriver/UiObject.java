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

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.el.WebElementPredicate;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPlugin;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectActionPluginService.actionPlugins;
import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectFindPluginService.findPlugins;
import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectInfoPluginService.infoPlugins;

public class UiObject extends HtmlElement implements HasLocator, WebElement, WrapsElement, Locatable, UiDriverPlugin {

	private By by;
	protected WebElement wrapped;
	protected int timeout = -1;

	public UiObject() {

	}

	public UiObject(By by) {
		this.by = by;
	}

	public UiObject(WebElement element) {
		this.wrapped = element;
	}

	public UiObject(WebElement element, By by) {
		this(element);
		this.by = by;
	}

	public UiObject(WebElement element, By by, String name) {
		this(element, by);
		setName(name);
	}

	public WebElement getWrappedElement() {
		if (wrapped == null) {
			Preconditions.checkNotNull(getLocator(), "Element and By cannot be null");
			if (timeout > 0){
				wrapped = DriverFactory.delay()
						.withTimeout(timeout, TimeUnit.SECONDS)
						.until(ExpectedConditions.presenceOfElementLocated(this.getLocator()));				
			} else {
				wrapped = DriverFactory.getDriver().findElement(getLocator());
			}
		}
		return wrapped;
	}

	public void setWrappedElement(WebElement element) {
		this.wrapped = element;
	}

	public By getLocator() {
		return by;
	}

	public void lowLevelClick() {
		Waits.withDefaultDelay().pollingEvery(200, TimeUnit.MILLISECONDS).until(new Predicate<WebDriver>() {
			@Override
			public boolean apply(WebDriver input) {
				try {
					DriverFactory.actions().moveToElement(getWrappedElement()).click().perform();
					return true;
				} catch (WebDriverException ignore) {
					return false;
				}
			}
		});
	}

	private static final WebElement unwrap(WebElement element){
		if (WrapsElement.class.isAssignableFrom(element.getClass())){
			return unwrap(((WrapsElement)element).getWrappedElement());
		} else return element;
	}
	public void click() {
		actionPlugins().beforeClick(this);
		try {
			if (ConfigLoader.config().getBoolean("webdriver.lowlevel.click", false)) {
				lowLevelClick();
			} else {
				try {
					
				DriverFactory.actions().moveToElement(unwrap(getWrappedElement())).perform();
				} catch (Throwable ignore){
					
				}
				Waits.waitUntilClickable(getWrappedElement()).click();
			}

			actionPlugins().afterClick(this);
		} catch (WebDriverException e) {
			if (e.getMessage().contains("click")) {
				try {
					Waits.withDefaultDelay().pollingEvery(200, TimeUnit.MILLISECONDS).until(new Predicate<WebDriver>() {
						@Override
						public boolean apply(WebDriver input) {
							try {
								DriverFactory.actions().moveToElement(getWrappedElement()).click().perform();
								return true;
							} catch (WebDriverException ignore) {
								return false;
							}
						}
					});
					actionPlugins().afterClick(this);
				} catch (TimeoutException ea) {
					throw e;
				}
			} else
				throw e;
		}

	}

	public boolean checkedClick() {
		actionPlugins().beforeClick(this);
		boolean result = false;
		try {

			Waits.waitUntilClickable(getWrappedElement()).click();
			getWrappedElement().click();
			result = true;
		} catch (WebDriverException e) {
		}
		actionPlugins().afterClick(this);
		return result;
	}

	public void submit() {
		actionPlugins().beforeSubmit(this);
		getWrappedElement().submit();
		actionPlugins().afterSubmit(this);

	}

	public void sendKeys(CharSequence... keysToSend) {
		actionPlugins().beforeSendKeys(this, keysToSend);
		try {
			getWrappedElement().sendKeys(keysToSend);
		} catch (InvalidElementStateException e){
			this.getWrappedElement();
			Page.scrollTo(this);
			click();
			this.wrapped = null;
			getWrappedElement().sendKeys(keysToSend);
			Waits.waitUntilClickable(this).sendKeys(keysToSend);
		}
		actionPlugins().afterSendKeys(this, keysToSend);
	}

	public void clearAndSendKeys(CharSequence... keysToSend){
		actionPlugins().beforeClear(this);
		clear();
		actionPlugins().afterClear(this);
		actionPlugins().beforeSendKeys(this, keysToSend);
		sendKeys(keysToSend);
		actionPlugins().afterSendKeys(this, keysToSend);
	}
	
	public void sendKeysIfDifferent(CharSequence... keysToSend){
		String currentText = getText();
		if (! Joiner.on("").join(keysToSend).equals(currentText)){
			clearAndSendKeys(keysToSend);
		}
	}
	public void clear() {
		actionPlugins().beforeClear(this);
		getWrappedElement().clear();
		actionPlugins().afterClear(this);

	}

	public String getTagName() {
		infoPlugins().beforeGetTagName(this);
		String value = getWrappedElement().getTagName();
		infoPlugins().afterGetTagName(this, value);
		return value;
	}

	public String getAttribute(String name) {
		infoPlugins().beforeGetAttribute(this, name);
		String value = getWrappedElement().getAttribute(name);
		infoPlugins().afterGetAttribute(this, name, value);
		return value;
	}

	public boolean isSelected() {
		infoPlugins().beforeIsSelected(this);
		boolean value = getWrappedElement().isSelected();
		infoPlugins().afterIsSelected(this, value);
		return value;
	}

	public boolean isEnabled() {
		infoPlugins().beforeIsEnabled(this);
		boolean value = getWrappedElement().isEnabled();
		infoPlugins().afterIsEnabled(this, value);
		return value;
	}

	public String getText() {
		infoPlugins().beforeGetText(this);
		String text = getWrappedElement().getText();
		infoPlugins().afterGetText(this, text);
		return text;
	}

	public static UiObject wrap(WebElement toBeWrapped, By by, String name) {
		if (toBeWrapped instanceof UiObject) {
			((UiObject) toBeWrapped).setName(name);
			return (UiObject) toBeWrapped;
		} else {
			return new UiObject(toBeWrapped, by, name);
		}
	}

	public static UiObject wrap(WebElement toBeWrapped, By by) {
		if (toBeWrapped instanceof UiObject) {
			return (UiObject) toBeWrapped;
		} else {
			return new UiObject(toBeWrapped, by);
		}
	}

	public static List<WebElement> wrapAll(List<WebElement> toBeWrapped, By by) {
		List<WebElement> result = Lists.newArrayList();
		for (WebElement element : toBeWrapped) {
			result.add(wrap(element, by));
		}
		return result;
	}

	public List<WebElement> findElements(By by) {
		findPlugins().beforeFindElements(this, by);
		List<WebElement> found = null;  
		try {
			found = new FilterableSearchContext(getWrappedElement()).findElements(by);
			Preconditions.checkState(! found.isEmpty());
		} catch (Throwable cachingIsNotWorking){
			found = DriverFactory.getDriver().findElements(new ByChained(getLocator(), by));
		}
		findPlugins().afterFindElements(this, by, found);
		return wrapAll(found, by);
	}

	public WebElement findElement(By by) {
		findPlugins().beforeFindElement(this, by);
		WebElement result = null;
		try {
			result = new FilterableSearchContext(getWrappedElement()).findElement(by); 
		} catch (Throwable cachingIsNotWorking){
			result = DriverFactory.getDriver().findElement(new ByChained(getLocator(), by));
		}
		findPlugins().afterFindElement(this, by, result);
		return wrap(result, by);
	}

	public boolean isDisplayed() {
		infoPlugins().beforeIsDisplayed(this);
		boolean value = getWrappedElement().isDisplayed();
		infoPlugins().afterIsDisplayed(this, value);
		return value;
	}

	public Point getLocation() {
		infoPlugins().beforeGetLocation(this);
		Point value = getWrappedElement().getLocation();
		infoPlugins().afterGetLocation(this, value);
		return value;
	}

	public Dimension getSize() {
		infoPlugins().beforeGetSize(this);
		Dimension value = getWrappedElement().getSize();
		infoPlugins().afterGetSize(this, value);
		return value;
	}

	public String getCssValue(String propertyName) {
		infoPlugins().beforeGetCssValue(this, propertyName);
		String value = getWrappedElement().getCssValue(propertyName);
		infoPlugins().afterGetCssValue(this, propertyName, value);
		return value;
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		infoPlugins().beforeGetScreenshotAs(this, target);
		X screenshot = getWrappedElement().getScreenshotAs(target);
		infoPlugins().afterGetScreenshotAs(this, target, screenshot);
		return screenshot;
	}

	@Override
	public Coordinates getCoordinates() {
		infoPlugins().beforeGetCoordinates(this);
		Coordinates result = ((Locatable) getWrappedElement()).getCoordinates();
		infoPlugins().afterGetCoordinates(this, result);
		return result;
	}

	@Override
	public Rectangle getRect() {
		infoPlugins().beforeGetRect(this);
		Rectangle result = getWrappedElement().getRect();
		infoPlugins().afterGetRect(this, result);
		return result;
	}

	@Override
	public String toString() {
		if (getName() != null) {
			return getName();
		} else if (by != null) {
			return by.toString();
		} else if (getWrappedElement() instanceof UiObject){
			return getWrappedElement().toString();
		} else 	{
			return super.toString();
		}
	}

	private void untilPredicate(final String predicate, final Function<WebElement, Void> action) {
		final WebElementPredicate validator = new WebElementPredicate(predicate);
		new FluentWait<WebElement>(this)
		.withTimeout(ConfigLoader.config().getLong("webdriver.click.check.timeout", 10000),
				TimeUnit.MILLISECONDS)
		.pollingEvery(1000, TimeUnit.MILLISECONDS).until(new Predicate<WebElement>() {

			private WebElement el;
			@Override
			public boolean apply(WebElement input) {
				try {
					el = input;
					action.apply(input);
					return validator.apply(input);
				} catch (Throwable e) {
					return false;
				}
			}

			public String toString(){
				return String.format("Waiting for predicate %s on %s", predicate,el);
			}
		});

	}

	public void clickUntil(final String predicate) {
		untilPredicate(predicate, new Function<WebElement, Void>() {

			@Override
			public Void apply(WebElement input) {
				input.click();
				return null;
			}
		});
	}

	public void setTimeout(int timeout){
		this.timeout = timeout;
	}

	public int getTimeout(){
		return timeout;
	}
	public void sendKeysUntil(final String predicate, final CharSequence... keys) {
		untilPredicate(predicate, new Function<WebElement, Void>() {

			@Override
			public Void apply(WebElement input) {
				input.sendKeys(keys);
				return null;
			}
		});
	}

	public UiObject getParent() {
		return UiObject.wrap(getWrappedElement().findElement(By.xpath("..")),new ByChained(getLocator(),By.xpath("..")));
	}

	public List<WebElement> getChildren() {
		return getWrappedElement().findElements(By.xpath(".//*"));
	}

	@Override
	public void beforeGetDriver() {


	}

	@Override
	public void afterGetDriver(WebDriver driver) {


	}

	@Override
	public void beforeCloseDriver(WebDriver driver) {


	}

	@Override
	public void afterCloseDriver() {
		wrapped = null;

	}

	@Override
	public void beforeInstantiateDriver() {
	}

	@Override
	public void afterInstantiateDriver(WebDriver driver) {

	}
	public byte[] screenshot(){
		try {
			return getWrappedElement().getScreenshotAs(OutputType.BYTES);
		} catch (WebDriverException e){
			return DriverFactory.getScreenshot();
		}
	}

}
