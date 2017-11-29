package com.automationrockstars.gir.ui;

import org.openqa.selenium.WebElement;

@FindBy(className="f")
public interface DescriptionDiv extends UiPart{

	@FindBy(tagName="span")
	@Filter("display != 'none'")
	WebElement span();
}
