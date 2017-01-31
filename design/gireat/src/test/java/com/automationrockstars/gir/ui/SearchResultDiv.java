package com.automationrockstars.gir.ui;

import org.openqa.selenium.WebElement;

import ru.yandex.qatools.htmlelements.element.Link;

@FindBy(className="g")
public interface SearchResultDiv extends UiPart {

	@FindBy(tagName="a")
	Link link();
	
	@FindBy(className="s")
	WebElement description();
	
}
