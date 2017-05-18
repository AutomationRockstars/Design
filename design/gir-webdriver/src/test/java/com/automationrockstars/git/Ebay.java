package com.automationrockstars.git;

import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class Ebay {

	
	static {
		DriverFactory.getDriver().get("http://www.ebay.ie/");
	}
	
	public static EbayPageHeader page(){
		return EbayPageHeader.waitFor();
	}
	public static String searchFor(String whatFor){
		return page().getText();
	}
}
