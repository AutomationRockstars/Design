/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

package com.automationrockstars.gir.ui;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.sikulix.SikulixDriverProvider;
import org.openqa.selenium.support.ui.FluentWait;
import ru.yandex.qatools.htmlelements.element.Link;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

public class UiPartsTest {

	
	@BeforeClass
	public static void prepare() {
		ConfigLoader.config().setProperty("noui", true);
	}

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
        result.results().filter(input ->
                input.getText().contains("github")).transform(input ->
                input.findElement(org.openqa.selenium.By.tagName("a"))).first().get().click();

        DriverFactory.closeDriver();
    }

    @Test
    public void should_useUiPartInsideUiPart() {
        doSearch();

        if (! System.getProperty("os.name").toLowerCase().contains("linux")) {
            SearchResults result = UiParts.get(SearchResults.class);
            assertThat(result.allResults().size(), is(greaterThan(5)));
            result.allResults().filter(input -> input.getText().contains("github")).filter(searchResultDiv -> {
                searchResultDiv.description().getText();
                return true;
            }).transform(input -> input.link()).first().get().click();
            DriverFactory.closeDriver();
        }
    }

    @Test
    public void lame() {
    	WebDriver driver = DriverFactory.getDriver();
        driver.get("http://www.google.ie");
        String html = driver.getPageSource();
        driver.findElement(org.openqa.selenium.By.name("q")).sendKeys("automationrockstars\n");
        WebElement d = new FluentWait<SearchContext>(driver)
                .withTimeout(5, TimeUnit.SECONDS)
                .until(new Function<SearchContext, WebElement>() {
                    @Nullable
                    @Override
                    public WebElement apply(@Nullable SearchContext searchContext) {
                        return searchContext.findElement(org.openqa.selenium.By.className("g"));
                    }
                });
        DriverFactory.closeDriver();
    }

    @Test
    public void should_beLogical() throws InterruptedException, IOException {
	    if (! System.getProperty("os.name").toLowerCase().contains("linux")) {
            assertThat(GoogleSearch.performSearch("automationrockstars").results()
                    .transform(input -> input.getText().replaceAll("\\n.*", "")), hasItem(containsString("AutomationRockstars")));

            UiParts.on(SearchResults.class).githubLink().click();
            DriverFactory.getDriver().navigate().back();
            DriverFactory.getDriver().navigate().refresh();
            GoogleSearch.performSearch("automationrockstars.com");
            if (UiParts.body().getText().length() < 100) {
                DriverFactory.closeDriver();
                GoogleSearch.performSearch("automationrockstars.com");
            }
            UiParts.on(SearchResults.class).arsLink().click();
            DriverFactory.closeDriver();
        }
    }

    @Test
    public void should_doTheMixThing() {
        doSearch();
        if (!ConfigLoader.config().containsKey("noui"))
            try {
                if (new SikulixDriverProvider().provide() != null) {
                    UiParts.on(SearchResults.class).googleLogo().getSize();
                }
            } catch (Exception ignore) {
                // TODO: handle exception
            }
    }
}
