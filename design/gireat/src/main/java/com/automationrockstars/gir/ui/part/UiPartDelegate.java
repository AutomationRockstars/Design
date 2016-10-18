package com.automationrockstars.gir.ui.part;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.design.gir.webdriver.FilterableSearchContext;
import com.automationrockstars.design.gir.webdriver.InitialPage;
import com.automationrockstars.design.gir.webdriver.Page;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.design.gir.webdriver.Waits;
import com.automationrockstars.gir.ui.Name;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.UiParts;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;


public class UiPartDelegate implements UiPart {

	private final Class<? extends UiPart> view;
	public UiPartDelegate(Class<? extends UiPart> view){
		this.view = view;
	}

	public String name() {
		if (view.getAnnotation(Name.class) != null){
			return view.getAnnotation(Name.class).value();
		} else {
			return String.format("UiPart %s", getLocator());
		}
	}

	public FluentIterable<UiObject> children() {
		return FluentIterable.from(this.findElements(org.openqa.selenium.By.xpath(".//*"))).transform(new Function<WebElement,UiObject>(){
			int instance = 0;

			public UiObject apply(WebElement input) {
				return UiObject.wrap(input, org.openqa.selenium.By.xpath(".//*["+(instance++) +"]"));
			}});

	}

	public boolean has(org.openqa.selenium.By element) {
		return Waits.visible(element).apply(FilterableSearchContext.unwrap(this))!= null;
	}

	public boolean hasText(String text) {
		return getText().contains(text);
	}

	public boolean isPresent() {
		if (Page.isElementPresent(getLocator()) ){
			return ! new FilterableSearchContext(DriverFactory.getUnwrappedDriver()).findElements(getLocator()).isEmpty();
		} else return false;

	}

	public boolean isVisible() {
		try {
			return Page.isElementVisible(getLocator());
		} catch (WebDriverException e){
			return false;
		}
	}

	private UiObject wrapped=null;
	public WebElement getWrappedElement() {
		if (view.getAnnotation(InitialPage.class) != null){
			if (view.getAnnotation(InitialPage.class).restartBrowser()){
				DriverFactory.closeDriver();
			}
			if (! DriverFactory.canScreenshot() || 
					view.getAnnotation(InitialPage.class).reload()
					|| ! DriverFactory.getDriver().getCurrentUrl().startsWith("http")){
				DriverFactory.getDriver().get(MoreObjects.firstNonNull(ConfigLoader.config().getString("url"), view.getAnnotation(InitialPage.class).url()));
			} 			
		}
		if (wrapped == null){
			wrapped = new UiObject(getLocator());
			wrapped.setName(name());
		} 
		return wrapped;
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


	public org.openqa.selenium.By getLocator() {
		return UiParts.buildBy(view);
	}

	public FluentWait<SearchContext> delay() {
		return new FluentWait<SearchContext>(getWrappedElement());
	}

	public String toString(){
		return String.format("UiPart %s", name());
	}

	@Override
	public void waitForHidden() {
		
		new FluentWait<UiPart>(this)
		.withTimeout(60, TimeUnit.SECONDS)
		.pollingEvery(200, TimeUnit.MILLISECONDS)
		.until(new Predicate<UiPart>() {
			
			@Override
			public boolean apply(UiPart input) {
				if (input.isPresent()){
					return ! input.isVisible();
				}
				return true; 
			}
		});
		
	}





}
