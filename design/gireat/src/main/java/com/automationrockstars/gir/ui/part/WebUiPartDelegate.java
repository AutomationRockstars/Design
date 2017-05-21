package com.automationrockstars.gir.ui.part;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
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
import com.automationrockstars.gir.ui.Timeout;
import com.automationrockstars.gir.ui.UiPart;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import ru.yandex.qatools.htmlelements.element.Link;


public class WebUiPartDelegate extends AbstractUiPartDelegate {

	public WebUiPartDelegate(Class<? extends UiPart> view) {
		super(view);
		initialPageSetUp();
	}

	public WebUiPartDelegate(Class<? extends UiPart> generic, UiObject toWrap) {
		super(generic, toWrap);
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


	public WebElement getWrappedElement() {
		if (! loaded.get()){
			initialPageSetUp();
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

	void initialPageSetUp() {
		if (view.getAnnotation(InitialPage.class) != null){
			if (view.getAnnotation(InitialPage.class).restartBrowser()){
				DriverFactory.closeDriver();
			}
			if (! DriverFactory.canScreenshot() ||
					view.getAnnotation(InitialPage.class).reload()
					|| ! DriverFactory.getDriver().getCurrentUrl().startsWith("http")){
				String url = (String) FluentIterable.from(Lists.newArrayList(DriverFactory.url(),
						ConfigLoader.config().getString("url"),
						view.getAnnotation(InitialPage.class).url())).firstMatch(new Predicate() {

					@Override
					public boolean apply( Object o) {
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
	}

	public void handleIeSpecifics(){
		if (DriverFactory.getDriver().getTitle().contains("Certificate Error")){
			DriverFactory.getDriver().findElement(By.name("overridelink")).click();
		}

	}
	public void handleEdgeSpecifics(){
		if (DriverFactory.getDriver().getTitle().contains("Certificate error")){
			DriverFactory.getDriver().findElement(By.id("invalidcert_continue")).click();
		}

	}
	public void handleBrowserSpecifics(){
		if (DriverFactory.isIe()){
			handleIeSpecifics();
		} else if (DriverFactory.isEdge()){
			handleEdgeSpecifics();
		}
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
