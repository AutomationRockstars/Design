package com.automationrockstars.git;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.FindBy;

import com.automationrockstars.design.gir.webdriver.UiFragment;

@FindBy(id="headerFragment")
public class EbayPageHeader  extends UiFragment{
	
	public static EbayPageHeader waitFor(){
		return new EbayPageHeader();
	}

	@Override
	public void setLocator(By by) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getRect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		// TODO Auto-generated method stub
		return null;
	}

}
