package com.automationrockstars.gir.ui.part;

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

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.Name;
import com.automationrockstars.gir.ui.UiPart;

public abstract class AbstractUiPartDelegate implements UiPart{
	protected final Class<? extends UiPart> view;
	
	private SearchContext parent;
	private By locator = null;
	
	public AbstractUiPartDelegate(Class<? extends UiPart> view){
		this.view = view;
		locator = UiPartProxy.buildBy(this,view);
	}
	
	public AbstractUiPartDelegate(Class<? extends UiPart> view,UiObject toWrap){
		this.view = view;
		this.wrapped = toWrap;
		locator = toWrap.getLocator();
		loaded.set(true);
	}
	public String name() {
		if (view.getAnnotation(Name.class) != null){
			return view.getAnnotation(Name.class).value();
		} else {
			return String.format("UiPart %s", getLocator());
		}
	}
	@Override
	public SearchContext parent() {
		return parent;
	}
	
	public void parent(SearchContext parent){
		this.parent = parent;
	}
	public FluentWait<SearchContext> delay() {
		return new FluentWait<SearchContext>(getWrappedElement());
	}
	public void click() {
		getWrappedElement().click();
	}

	public void submit() {
		getWrappedElement().submit();
	}

	public void sendKeys(CharSequence... keysToSend) {
		getWrappedElement().sendKeys(keysToSend);
	}

	public void clear() {
		getWrappedElement().clear();
	}

	public String getTagName() {
		return getWrappedElement().getTagName();
	}

	public String getAttribute(String name) {
		return getWrappedElement().getAttribute(name);
	}

	public boolean isSelected() {
		return getWrappedElement().isSelected();
	}

	public boolean isEnabled() {
		return getWrappedElement().isEnabled();
	}

	public String getText() {
		return getWrappedElement().getText();
	}

	public List<WebElement> findElements(org.openqa.selenium.By by) {
		return getWrappedElement().findElements(by);
	}

	public WebElement findElement(org.openqa.selenium.By by) {
		return getWrappedElement().findElement(by);
	}

	public boolean isDisplayed() {
		return getWrappedElement().isDisplayed();
	}

	public Point getLocation() {
		return getWrappedElement().getLocation();
	}

	public Dimension getSize() {
		return getWrappedElement().getSize();
	}

	public Rectangle getRect() {
		return getWrappedElement().getRect();
	}

	public String getCssValue(String propertyName) {
		return getWrappedElement().getCssValue(propertyName);
	}

	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		return getWrappedElement().getScreenshotAs(target);
	}
	protected UiObject wrapped=null;
	protected static ThreadLocal<Boolean> loaded = new ThreadLocal<Boolean>(){

		@Override
		protected Boolean initialValue(){
			return Boolean.FALSE;
		}
	};
	public org.openqa.selenium.By getLocator() {
		return locator;
	}



	public String toString(){
		return String.format("UiPart %s", name());
	}
}
