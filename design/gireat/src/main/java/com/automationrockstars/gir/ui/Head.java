package com.automationrockstars.gir.ui;

import org.openqa.selenium.WebElement;

import com.google.common.collect.FluentIterable;

@FindBy(tagName="head")
@Covered(lookForVisibleParent=false)
public interface Head extends UiPart {

	@FindBy(tagName="meta")
	@Covered(lookForVisibleParent=false)
	FluentIterable<WebElement> meta();
	
	@FindBy(tagName="script")
	@Covered(lookForVisibleParent=false)
	FluentIterable<WebElement> script();
	
	@FindBy(tagName="style")
	@Covered(lookForVisibleParent=false)
	FluentIterable<WebElement> style();

	@FindBy(tagName="link")
	@Covered(lookForVisibleParent=false)
	FluentIterable<WebElement> link();
	
}
