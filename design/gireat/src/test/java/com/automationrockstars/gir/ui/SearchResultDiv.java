package com.automationrockstars.gir.ui;

import org.openqa.selenium.WebElement;

import ru.yandex.qatools.htmlelements.element.Link;

@WithFindByAugmenter(TestFixingAugmenter.class)
@FindBy(className="removemeg")
public interface SearchResultDiv extends UiPart {
	
	@WithFindByAugmenter(TestFixingAugmenter.class)
	@FindBy(tagName="a")
	Link link();
	
	@WithFindByAugmenter(TestFixingAugmenter.class)
	@FindBy(className="removemes")
	WebElement description();
	
}
