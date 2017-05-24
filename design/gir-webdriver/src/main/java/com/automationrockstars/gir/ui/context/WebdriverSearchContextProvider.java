package com.automationrockstars.gir.ui.context;

import java.lang.annotation.Annotation;

import org.openqa.selenium.SearchContext;

import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class WebdriverSearchContextProvider implements SearchContextProvider{

	@Override
	public boolean canProvide(Class<? extends Annotation> context) {
		return Web.class.equals(context);
	}

	@Override
	public SearchContext provide() {
		return DriverFactory.getDriver();
	}

}
