package com.automationrockstars.gir.ui;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import javax.annotation.Nullable;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.sikulix.SikulixDriver;
import org.openqa.selenium.sikulix.SikulixDriverProvider;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import ru.yandex.qatools.htmlelements.element.Link;

public class UiPartsTest {


	

	private void doSearch() {
		GoogleHome init = UiParts.get(GoogleHome.class);
		init.query().clear();
		init.query().sendKeys("automationrockstars");
		init.search().click();
	}

	@Test
	public void should_doTheThing() {
		doSearch();

		SearchResults result = UiParts.get(SearchResults.class);
		assertThat(result.results().size(), is(greaterThan(5)));
		assertThat(result.links().size(), is(greaterThan(5)));
		result.results().filter(new Predicate<WebElement>() {

			public boolean apply(WebElement input) {
				return input.getText().contains("github");
			}
		}).transform(new Function<WebElement, WebElement>() {

			public WebElement apply(WebElement input) {
				return input.findElement(org.openqa.selenium.By.tagName("a"));
			}
		}).first().get().click();

		DriverFactory.closeDriver();
	}

	@Test
	public void should_useUiPartInsideUiPart() {
		doSearch();

		SearchResults result = UiParts.get(SearchResults.class);
		assertThat(result.allResults().size(), is(greaterThan(5)));
		result.allResults().filter(new Predicate<SearchResultDiv>() {

			@Override
			public boolean apply(SearchResultDiv input) {
				return input.getText().contains("github");
			}
		}).filter(new Predicate<SearchResultDiv>() {
			@Override
			public boolean apply(@Nullable SearchResultDiv searchResultDiv) {
				searchResultDiv.description().getText();
				return true;
			}
		}).transform(new Function<SearchResultDiv, Link>() {

			@Override
			public Link apply(SearchResultDiv input) {

				return input.link();
			}

		}).first().get().click();
		DriverFactory.closeDriver();
	}

	@Test
	public void should_beLogical() throws InterruptedException, IOException {
		assertThat(GoogleSearch.performSearch("automationrockstars").results()
				.transform(new Function<WebElement, String>() {

					public String apply(WebElement input) {
						return input.getText().replaceAll("\\n.*", "");
					}
				}), hasItem(containsString("AutomationRockstars")));

		UiParts.on(SearchResults.class).githubLink().click();
		DriverFactory.getDriver().navigate().back();
		if (UiParts.body().getText().length() < 100) {
			GoogleSearch.performSearch("automationrockstars");
			if (UiParts.body().getText().length() < 100){
				DriverFactory.closeDriver();
				GoogleSearch.performSearch("automationrockstars");
			}
		}

		UiParts.on(SearchResults.class).arsLink().click();
		DriverFactory.closeDriver();
	}

	@Test
	public void should_doTheMixThing() {
		doSearch();
		if (!ConfigLoader.config().containsKey("noui"))
			try {
				if (new SikulixDriverProvider().provide() != null){
					UiParts.on(SearchResults.class).googleLogo().getSize();
				}
			} catch (Exception ignore) {
				// TODO: handle exception
			}
	}
}
