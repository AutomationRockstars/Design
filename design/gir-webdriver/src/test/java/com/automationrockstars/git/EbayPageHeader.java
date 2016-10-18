package com.automationrockstars.git;

import org.openqa.selenium.support.FindBy;

import com.automationrockstars.design.gir.webdriver.UiFragment;

@FindBy(id="headerFragment")
public class EbayPageHeader  extends UiFragment{
	
	public static EbayPageHeader waitFor(){
		return new EbayPageHeader();
	}

}
