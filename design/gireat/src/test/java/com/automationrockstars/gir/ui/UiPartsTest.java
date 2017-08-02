package com.automationrockstars.gir.ui;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.machinepublishers.jbrowserdriver.*;
import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.sikulix.SikulixDriver;
import org.openqa.selenium.sikulix.SikulixDriverProvider;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import ru.yandex.qatools.htmlelements.element.Link;

public class UiPartsTest {


	

	private void doSearch() {
		GoogleHome init = UiParts.get(GoogleHome.class);
		init.query().clear();
		init.query().getWrappedElement().click();
		init.query().sendKeys("automationrockstars");
		init.search().click();
	}


	@Test(timeout = 30000)
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
    public void lame(){
        Settings settings = Settings.builder().proxy(new ProxyConfig(ProxyConfig.Type.HTTP,"localhost",3128)).headless(false).timezone(Timezone.AMERICA_NEWYORK).build();
        JBrowserDriver driver = new JBrowserDriver(settings);
        driver.get("http://www.google.ie");
        System.out.println("status code: " + driver.getStatusCode());
        String html = driver.getPageSource();
        driver.findElement(org.openqa.selenium.By.name("q")).sendKeys("automationrockstars\n");
        WebElement d = new FluentWait<SearchContext>(driver)
                .withTimeout(5, TimeUnit.SECONDS)
                .until(new Function<SearchContext,WebElement>() {
            @Nullable
            @Override
            public WebElement apply(@Nullable SearchContext searchContext) {
                return searchContext.findElement(org.openqa.selenium.By.className("g"));
            }
        });
        System.out.println("DD" + d.getText());
        driver.quit();
        System.out.println(html);
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
		
			GoogleSearch.performSearch("automationrockstars.com");
			if (UiParts.body().getText().length() < 100){
				DriverFactory.closeDriver();
				GoogleSearch.performSearch("automationrockstars.com");
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
