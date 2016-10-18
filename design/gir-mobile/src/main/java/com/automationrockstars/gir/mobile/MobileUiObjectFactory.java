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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.design.gir.webdriver.CanVerify;
import com.automationrockstars.design.gir.webdriver.HasLocator;
import com.automationrockstars.design.gir.webdriver.HasWaits;
import com.automationrockstars.gir.mobile.ByTextInBetween.BetweenBuilder;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;

public class MobileUiObjectFactory {

	public static ByVisibleText visibleText(String... text){
		return ByVisibleText.visibleText(text);
	}
	
	public static ByVisibleText visibleTextPattern(String pattern){
		return ByVisibleText.visibleTextPattern(pattern);
	}
	
	public static BetweenBuilder visiblePart(String... text){
		return ByTextInBetween.visiblePart(text);
	}
	public static MobileUiObject from(By by){
		return new MobileUiObject(MobileFactory.currentPlatform(), by);
	}
	public static MobileUiObject from(WebElement element, By by){
		return new MobileUiObject(element, by);
	}
	public static MobileUiObject from(WebElement element, By by, String name){
		return new MobileUiObject(element, by, name);
	}
	public static MobileUiObject from(String... filter){
	return new MobileUiObject(MobileFactory.currentPlatform(),filter);
	}
	public static MobileUiObject from(Map<String,?> mutltiPlatform){
		MobileUiObject result = new MobileUiObject();
		for (Map.Entry<String, ?> entry : mutltiPlatform.entrySet()){
			By newValue = null;
			if (entry.getValue() instanceof By){
				newValue = (By) entry.getValue();
			} else {
				if (MobileFactory.ANDROID.equals(entry.getKey())){
					if (entry.getValue() instanceof String){
						newValue = new ByVisibleTextAndroid((String)entry.getValue());
					} else {
						newValue = new ByVisibleTextAndroid((String[])entry.getValue());
					}
				} else {
					if (entry.getValue() instanceof String){
						newValue = new ByVisibleTextIos((String)entry.getValue());
					} else {
						newValue = new ByVisibleTextIos((String[])entry.getValue());
					}
				}
			}
			result.setLocator(entry.getKey(), newValue);
		}
		return result;
	}
	private static final Gson parser = new Gson();
	private static final Logger LOG = LoggerFactory.getLogger(MobileUiObjectFactory.class);
	
	public static MobileUiObject from(String multiPlatform){
		MobileUiObject result = new MobileUiObject();
		if (! multiPlatform.trim().startsWith("{")){
			multiPlatform = "{" + multiPlatform;
		}
		if (! multiPlatform.trim().endsWith("}")){
			multiPlatform = multiPlatform + "}";
		}
		LOG.debug("Using string {}",multiPlatform);
		@SuppressWarnings("unchecked")
		Map<String,String> platforms = parser.fromJson(multiPlatform, HashMap.class);
		for (Map.Entry<String,String> platform : platforms.entrySet()){
			if (MobileFactory.ANDROID.equalsIgnoreCase(platform.getKey())){
				result.setFilter(MobileFactory.ANDROID, platform.getValue().split("&&"));
			} if (MobileFactory.IOS.equalsIgnoreCase(platform.getKey())){
				result.setFilter(MobileFactory.IOS, platform.getValue().split("&&"));		
			}
		}
		return result;
	}
	
	
	public static UiObjectBuilder onAndroid(String... locator){
		return new UiObjectBuilder().onAndroid(locator);
	}
	public static UiObjectBuilder onIPhone(String... locator){
		return new UiObjectBuilder().onIPhone(locator);
	}
	public static UiObjectBuilder onIOs(String... locator){
		return new UiObjectBuilder().onIOs(locator);
	}
	
	public static UiObjectBuilder onAny(String... locator){
		return new UiObjectBuilder().onAndroid(locator).onIOs(locator);
	}
	
	public static UiObjectBuilder onAndroid(By by){
		return new UiObjectBuilder().onAndroid(by);
	}
	public static UiObjectBuilder onAny(By by){
		return new UiObjectBuilder().onAndroid(by).onIPhone(by);
	}
	public static UiObjectBuilder onIOs(By by){
		return new UiObjectBuilder().onIOs(by);
	}
	
	public static UiObjectBuilder onIPhone(By by){
		return new UiObjectBuilder().onIPhone(by);
	}
	
	public static class UiObjectBuilder implements HasWaits, WebElement, WrapsElement, CanSwipe, HasLocator, CanVerify {
		private MobileUiObject wrapped = null;
		public MobileUiObject getWrappedElement(){
			if (wrapped == null){
				wrapped = new MobileUiObject();
			}
			return wrapped;
		}	
		public UiObjectBuilder onAndroid(String... locator){
			getWrappedElement().setFilter(MobileFactory.ANDROID, locator);
			return this;
		}
		
		public UiObjectBuilder onAndroid(By by){
			getWrappedElement().setLocator(MobileFactory.ANDROID, by);
			return this;
		}
		
		public UiObjectBuilder onIPhone(String... filter){
			return this.onIOs(filter);
		}
		public UiObjectBuilder onIOs(String... filter){
			getWrappedElement().setFilter(MobileFactory.IOS, filter);
			return this;
		}
		public UiObjectBuilder onIPhone(By by){
			getWrappedElement().setLocator(MobileFactory.IOS, by);
			return this;
		}
		public UiObjectBuilder onIOs(By by) {
			return this.onIPhone(by);
		}
		@Override
		public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
			return getWrappedElement().getScreenshotAs(target);
		}
		@Override
		public void click() {
			getWrappedElement().click();
			
		}
		@Override
		public void submit() {
			getWrappedElement().click();			
		}
		@Override
		public void sendKeys(CharSequence... keysToSend) {
			getWrappedElement().sendKeys(keysToSend);
			
		}
		@Override
		public void clear() {
			getWrappedElement().clear();
			
		}
		@Override
		public String getTagName() {
			return getWrappedElement().getTagName();
		}
		@Override
		public String getAttribute(String name) {
			return getAttribute(name);
		}
		@Override
		public boolean isSelected() {
			return getWrappedElement().isSelected();
		}
		@Override
		public boolean isEnabled() {
			return getWrappedElement().isEnabled();
		}
		@Override
		public String getText() {
			return getWrappedElement().getText();
		}
		@Override
		public List<WebElement> findElements(By by) {
			return getWrappedElement().findElements(by);
		}
		@Override
		public WebElement findElement(By by) {
			return getWrappedElement().findElement(by);
		}
		public boolean isPresent(){
			return getWrappedElement().isPresent();
		}
		@Override
		public boolean isDisplayed() {
			return getWrappedElement().isDisplayed();
		}
		@Override
		public Point getLocation() {
			return getWrappedElement().getLocation();
		}
		@Override
		public Dimension getSize() {
			return getWrappedElement().getSize();
		}
		@Override
		public String getCssValue(String propertyName) {
			return getWrappedElement().getCssValue(propertyName);
		}
		@Override
		public void waitForVisible() {
			getWrappedElement().waitForVisible();
			
		}
		@Override
		public void waitForPresent() {
			getWrappedElement().waitForPresent();
			
		}
		
		public MobileUiObject build(){
			return getWrappedElement();
		}
		@Override
		public void swipeLeft() {
			getWrappedElement().swipeLeft();
			
		}
		@Override
		public void swipeRight() {
			getWrappedElement().swipeRight();
			
		}
		@Override
		public void swipeUp() {
			getWrappedElement().swipeUp();;
			
		}
		@Override
		public void swipeDown() {
			getWrappedElement().swipeDown();
			
		}
		@Override
		public By getLocator() {
			return getWrappedElement().getLocator();
		}
		@Override
		public void verifyVisible() {
			getWrappedElement().verifyVisible();
			
		}
		@Override
		public void verifyVisible(String failMessage) {
			getWrappedElement().verifyVisible(failMessage);
			
		}
		@Override
		public void verifyVisible(String failMessage, HasLocator errorObject) {
			getWrappedElement().verifyVisible(failMessage, errorObject);
			
		}
		@Override
		public void verifyVisible(String failMessage, By errorObject) {
			getWrappedElement().verifyVisible(failMessage, errorObject);
			
		}
		@Override
		public Rectangle getRect() {
			return getWrappedElement().getRect();
		}
		
		public List<MobileUiObject> all(){
			return MobileSearchUtils.findAllVisible(getLocator());
		}
		
		public MobileUiObject first(){
			return Iterables.getFirst(all(),null);
		}
		
		public MobileUiObject last(){
			return Iterables.getLast(all());
		}
		
		public MobileUiObject get(int i){
			return all().get(i);
		}
		
	}
	
}
