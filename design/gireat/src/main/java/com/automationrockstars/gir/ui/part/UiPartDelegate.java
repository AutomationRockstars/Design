package com.automationrockstars.gir.ui.part;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.FluentWait;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.design.gir.webdriver.FilterableSearchContext;
import com.automationrockstars.design.gir.webdriver.InitialPage;
import com.automationrockstars.design.gir.webdriver.Page;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.design.gir.webdriver.Waits;
import com.automationrockstars.gir.ui.FilteredBy;
import com.automationrockstars.gir.ui.Name;
import com.automationrockstars.gir.ui.Timeout;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.UiParts;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import ru.yandex.qatools.htmlelements.element.Link;

import javax.annotation.Nullable;


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
	private static ThreadLocal<Boolean> loaded = new ThreadLocal<Boolean>(){

		@Override
		protected Boolean initialValue(){
			return Boolean.FALSE;
		}
	};
	public WebElement getWrappedElement() {
		if (view.getAnnotation(InitialPage.class) != null){
			if (view.getAnnotation(InitialPage.class).restartBrowser()){
				DriverFactory.closeDriver();
			}
			if (! DriverFactory.canScreenshot() || 
					view.getAnnotation(InitialPage.class).reload()
					|| ! DriverFactory.getDriver().getCurrentUrl().startsWith("http")){
				String url = FluentIterable.from(Lists.newArrayList(DriverFactory.url(),
						ConfigLoader.config().getString("url"),
						view.getAnnotation(InitialPage.class).url())).firstMatch(new Predicate() {

					@Override
					public boolean apply(@Nullable Object o) {
						return o != null;
					}
				}).get();
				DriverFactory.getDriver().get(url);
			} 			
			if (! loaded.get()){				
				String desiredUrl = ConfigLoader.config().getString("url",view.getAnnotation(InitialPage.class).url());
				if (desiredUrl != null && ! DriverFactory.getDriver().getCurrentUrl().contains(desiredUrl)){
					DriverFactory.getDriver().get(desiredUrl);
				}
				loaded.set(Boolean.TRUE);			
			}
			handleBrowserSpecifics();
		}
		if (wrapped == null){
			wrapped = new UiObject(getLocator());
			wrapped.setName(name());
			if (view.getAnnotation(Timeout.class) != null ){
				wrapped.setTimeout(view.getAnnotation(Timeout.class).value());
			} else if (view.getAnnotation(ru.yandex.qatools.htmlelements.annotations.Timeout.class)!= null){
				wrapped.setTimeout(view.getAnnotation(ru.yandex.qatools.htmlelements.annotations.Timeout.class).value());
			}
		} 
		return wrapped;
	}

	public void handleIeSpecifics(){
		if (DriverFactory.getDriver().getTitle().contains("Certificate Error")){
			DriverFactory.getDriver().findElement(By.name("overridelink")).click();
		}

	}
	public void handleBrowserSpecifics(){
		if (DriverFactory.isIe()){
			handleIeSpecifics();
		}
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
			
			@Override
			public String toString(){
				return String.format("UiPart %s hidden", name());
			}
		});

	}

	@Override
	public UiObject childWithText(String text) {
		return waitForChild(new FilteredBy(By.xpath(".//*"), "text.contains('"+text+"')"));
		
	}

	private UiObject waitForChild(By childBy){
		final By theChildBy = new ByChained(getLocator(),childBy);
		return UiObject.wrap(delay().until(new Function<SearchContext,WebElement>() {
			@Override
			public WebElement apply(SearchContext input) {
				return input.findElement(theChildBy);
			}
		}), theChildBy);
	}
	
	@Override
	public Link childLinkWithText(String text) {
		return new Link( waitForChild(By.partialLinkText(text))); 
	}





}
