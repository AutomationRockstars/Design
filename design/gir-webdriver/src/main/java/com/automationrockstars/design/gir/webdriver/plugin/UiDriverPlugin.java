package com.automationrockstars.design.gir.webdriver.plugin;

import org.openqa.selenium.WebDriver;

public interface UiDriverPlugin {

	void beforeGetDriver();
	
	void afterGetDriver(WebDriver driver);
	
	void beforeCloseDriver(WebDriver driver);
	
	void afterCloseDriver();
}
