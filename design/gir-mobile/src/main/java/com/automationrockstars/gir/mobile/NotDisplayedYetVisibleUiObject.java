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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsElement;

class NotDisplayedYetVisibleUiObject implements WebElement, WrapsElement {

	private final String pageLine;
	private final WebElement wrapped;
	public NotDisplayedYetVisibleUiObject(String pageLine, WebElement proxied) {
		this.pageLine= pageLine;
		this.wrapped = proxied;
	}
	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		return wrapped.getScreenshotAs(target);
	}

	private String getValueFromLine(String name){
		return pageLine.replaceAll("^.* "+name+"=\"", "").replaceAll("\".*", "");
	}
	private int getX(){
		if (pageLine.contains(" x=")){
		return Integer.valueOf(getValueFromLine("x"));
		} else return wrapped.getLocation().x;
	}
	private int getY(){
		if (pageLine.contains(" y=")){
			return Integer.valueOf(getValueFromLine("y"));
			} else return wrapped.getLocation().y;
	}
	@Override
	public void click() {
		MobileUtils.press(getX(), getY());
	}

	@Override
	public void submit() {
		clear();
		
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		click();
		new Actions(MobileFactory.getDriver()).sendKeys(keysToSend).build().perform();	
	}

	@Override
	public void clear() {
		wrapped.clear();		
			}

	@Override
	public String getTagName() {
		return wrapped.getTagName();
	}

	@Override
	public String getAttribute(String name) {
		try {
			return wrapped.getAttribute(name);
		} catch (Throwable t){
			return getValueFromLine(name);
		}
	}

	@Override
	public boolean isSelected() {
			return wrapped.isSelected();
	}

	@Override
	public boolean isEnabled() {
		return wrapped.isEnabled();
	}

	@Override
	public String getText() {
return wrapped.getText();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return wrapped.findElements(by);
	}

	@Override
	public WebElement findElement(By by) {
		return wrapped.findElement(by);
	}

	@Override
	public boolean isDisplayed() {
		return wrapped.isDisplayed();
	}

	@Override
	public Point getLocation() {
		return new Point(getX(), getY());
	}

	@Override
	public Dimension getSize() {
		return wrapped.getSize();
	}

	@Override
	public String getCssValue(String propertyName) {
		return wrapped.getCssValue(propertyName);
	}
	@Override
	public WebElement getWrappedElement() {
		return wrapped;
	}
	@Override
	public Rectangle getRect() {
		return wrapped.getRect();
	}

}
