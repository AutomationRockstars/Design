package com.automationrockstars.gir.ui;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public abstract class AbstractWebElementDecorator implements WebElementDecorator{

	private final WebElement inner;
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		return inner.getScreenshotAs(target);
	}
	public String getAttribute(String name) {
		return inner.getAttribute(name);
	}
	public boolean isSelected() {
		return inner.isSelected();
	}
	public boolean isEnabled() {
		return inner.isEnabled();
	}
	public String getText() {
		return inner.getText();
	}
	public List<WebElement> findElements(By by) {
		return inner.findElements(by);
	}
	public WebElement findElement(By by) {
		return inner.findElement(by);
	}
	public boolean isDisplayed() {
		return inner.isDisplayed();
	}
	public Point getLocation() {
		return inner.getLocation();
	}
	public Dimension getSize() {
		return inner.getSize();
	}
	public Rectangle getRect() {
		return inner.getRect();
	}
	public String getCssValue(String propertyName) {
		return inner.getCssValue(propertyName);
	}
	public AbstractWebElementDecorator(WebElement inner) {
		this.inner = inner;
	}
	@Override
	public WebElement getWrappedElement() {
		return inner;
	}

	@Override
	public void click() {
		inner.click();
	}

	@Override
	public void submit() {
		inner.submit();
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		inner.sendKeys(keysToSend);
	}

	@Override
	public void clear() {
		inner.clear();
	}

	@Override
	public String getTagName() {
		return inner.getTagName();
	}


}
