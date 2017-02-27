package com.automationrockstars.gir.desktop.internal;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.automationrockstars.design.desktop.driver.ByImage;
import com.automationrockstars.design.desktop.driver.FindByImage;
import com.automationrockstars.design.desktop.driver.ImageUiObject;
import com.automationrockstars.design.desktop.driver.internal.SikuliDriver;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.desktop.ExtendedUiPart;
import com.automationrockstars.gir.ui.Name;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

import ru.yandex.qatools.htmlelements.element.Link;

public class ScreenProxyDelegate implements ExtendedUiPart  {

	private final Class<? extends ExtendedUiPart> view;
	
	public ScreenProxyDelegate(Class<? extends ExtendedUiPart> clazz) {
		this.view = clazz;
	}
	@Override
	public String name() {
		if (view.getAnnotation(Name.class) != null){
			return view.getAnnotation(Name.class).value();
		} else {
			return String.format("ExtendedUiPart %s", getLocator());
		}	}
	@Override
	public FluentIterable<UiObject> children() {
		throw new IllegalStateException("Not suported for Image type of objects");
	}
	@Override
	public boolean has(By element) {
		Preconditions.checkArgument(element instanceof ByImage);
		if (getLocator() != null){
			return ! getWrappedElement().findElements(element).isEmpty();
		} else {
			return SikuliDriver.isVisible(((ByImage)element).path()) != null;
		}
	}
	@Override
	public boolean hasText(String text) {
		if (getWrappedElement() != null){
			String elementText = getWrappedElement().getText();
			if (elementText != null){
				return elementText.contains(text);
			} else return false;
		}
		return false;
	}
	@Override
	public boolean isPresent() {
		if (getLocator() != null){
			return getWrappedElement().isDisplayed();
		} else return true;
	}
	@Override
	public boolean isVisible() {
		return isPresent();
	}
	@Override
	public FluentWait<SearchContext> delay() {
		if (getLocator() != null){
			return new FluentWait<SearchContext>(getWrappedElement());
		} else return new FluentWait<SearchContext>(SikuliDriver.driver());
	}
	@Override
	public void waitForHidden() {
		if (getLocator() != null){
			((ImageUiObject)getWrappedElement()).waitUntilHidden();
		} else throw new IllegalStateException("Not suported for whole screen");
		
	}
	@Override
	public UiObject childWithText(String text) {
		throw new IllegalStateException("Not suported for Image type of objects");
	}
	@Override
	public Link childLinkWithText(String text) {
		throw new IllegalStateException("Not suported for Image type of objects");
	}
	
	private ImageUiObject wrapped = null;
	@Override
	public WebElement getWrappedElement() {
		if (getLocator() != null){
			if (wrapped == null){
				wrapped = (ImageUiObject) SikuliDriver.driver().findElement(getLocator());
			}
		}
		return wrapped;
		
	}
	@Override
	public void click() {
		if (getWrappedElement() != null){
			getWrappedElement().click();
		}
		
	}
	@Override
	public void submit() {
		click();
		
	}
	@Override
	public void sendKeys(CharSequence... keysToSend) {
		if (getWrappedElement() != null){
			getWrappedElement().sendKeys(keysToSend);;
		}
		
	}
	@Override
	public void clear() {
		if (getWrappedElement() != null){
			getWrappedElement().clear();
		}
		
	}
	@Override
	public String getTagName() {
		if (getLocator() != null){
			return "image";
		} else return "screen";
	}
	@Override
	public String getAttribute(String name) {
		if (getWrappedElement() != null){
			return getWrappedElement().getAttribute(name);
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public boolean isSelected() {
		if (getWrappedElement() != null){
			return getWrappedElement().isSelected();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public boolean isEnabled() {
		if (getWrappedElement() != null){
			return getWrappedElement().isEnabled();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public String getText() {
		if (getWrappedElement() != null){
			return getWrappedElement().getText();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public List<WebElement> findElements(By by) {
		Preconditions.checkArgument(by instanceof ByImage);
		if (getLocator() != null){
			return  getWrappedElement().findElements(by);
		} else {
			return SikuliDriver.driver().findElements(by);
		}
	}
	@Override
	public WebElement findElement(By by) {
		Preconditions.checkArgument(by instanceof ByImage);
		if (getLocator() != null){
			return  getWrappedElement().findElement(by);
		} else {
			return SikuliDriver.driver().findElement(by);
		}
	}
	@Override
	public boolean isDisplayed() {
		if (getWrappedElement() != null){
			return getWrappedElement().isDisplayed();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public Point getLocation() {
		if (getWrappedElement() != null){
			return getWrappedElement().getLocation();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public Dimension getSize() {
		if (getWrappedElement() != null){
			return getWrappedElement().getSize();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public Rectangle getRect() {
		if (getWrappedElement() != null){
			return getWrappedElement().getRect();
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public String getCssValue(String propertyName) {
		throw new IllegalStateException("Not suported for Image type of objects");
	}
	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		if (getWrappedElement() != null){
			return getWrappedElement().getScreenshotAs(target);
		} else throw new IllegalStateException("Not suported for whole screen");
	}
	@Override
	public By getLocator() {
		FindByImage locator = view.getAnnotation(FindByImage.class); 
		if (locator != null){
			return new ByImage(locator.value());
		} else return null;
	}
	
	
}
