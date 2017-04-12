package com.automationrockstars.design.gir.webdriver;

import org.openqa.selenium.SearchContext;

public interface HasParent {

	SearchContext getParent();
	void setParent(SearchContext parent);
}
