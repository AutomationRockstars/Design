package com.automationrockstars.gir.ui;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.io.Files;


public class UiPartsTest {


	@Test
	public void keepChecking() throws IOException, InterruptedException{
		if (ConfigLoader.config().containsKey("noui")){
			int res = -1;
			try {
				res = new ProcessBuilder("phantomjs").start().waitFor(); 
			} catch (Exception e){
				res = -1;
			}
			if (res == 0){
				ConfigLoader.config().setProperty("webdriver.browser", "phantomjs");
			} else {
				return ;
			}
		}
		for (int i=0;i<2;i++){
			Long init = System.currentTimeMillis();
		should_doTheThing();
		should_beLogical();
		Files.append("Loop " + i + ": " +(System.currentTimeMillis() - init)  + " : " + new Date() + "\n", Paths.get("ts").toFile(), Charset.defaultCharset());
		}
	}
	
	
	@Test
	public void should_doTheThing() {
		if (ConfigLoader.config().containsKey("noui")){
			int res = -1;
			try {
				res = new ProcessBuilder("phantomjs").start().waitFor(); 
			} catch (Exception e){
				res = -1;
			}
			if (res == 0){
				ConfigLoader.config().setProperty("webdriver.browser", "phantomjs");
			} else {
				return ;
			}
		}
		GoogleHome init = UiParts.get(GoogleHome.class);
		init.query().sendKeys("automationrockstars");
		init.search().click();

		SearchResults result = UiParts.get(SearchResults.class);
		assertThat(result.results().size(),is(greaterThan(5)));
		assertThat(result.links().size(),is(greaterThan(5)));
		result.results().filter(new Predicate<WebElement>(){

			public boolean apply(WebElement input) {
				return input.getText().contains("automationrockstars.com");
			}} ).transform(new Function<WebElement, WebElement>() {

				public WebElement apply(WebElement input) {
					return input.findElement(org.openqa.selenium.By.tagName("a"));
				}
			}).first().get().click();
		System.out.println(DriverFactory.getDriver().getCurrentUrl());
		DriverFactory.closeDriver();
	}

	@Test
	public void should_beLogical() throws InterruptedException, IOException{
		if (ConfigLoader.config().containsKey("noui")){
			int res = -1;
			try {
				res = new ProcessBuilder("phantomjs").start().waitFor(); 
			} catch (Exception e){
				res = -1;
			}
			if (res == 0){
				ConfigLoader.config().setProperty("webdriver.browser", "phantomjs");
			} else {
				return ;
			}
		}
	
		assertThat(GoogleSearch.performSearch("automationrockstars").results().transform(new Function<WebElement,String>(){

			public String apply(WebElement input) {
				return input.getText().replaceAll("\\n.*", "");
			}}),hasItem(containsString("AutomationRockstars · GitHub")));
		
		
		UiParts.on(SearchResults.class).githubLink().click();
		DriverFactory.getDriver().navigate().back();
		UiParts.on(SearchResults.class).arsLink().click();
		DriverFactory.closeDriver();
	}

}
