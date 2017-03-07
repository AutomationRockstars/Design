package com.automationrockstars.gir.ui;

import java.util.List;

import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.WebElement;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.context.Image;
import com.google.common.collect.FluentIterable;

import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

@org.openqa.selenium.support.FindBy(id="res")
@Name("Search Results")
public interface SearchResults extends UiPart {

	@Find(@By(how=ByClassName.class,using="g"))
	FluentIterable<WebElement> results();

	@FindAll({
			@FindBy(tagName="a"),
			@FindBy(tagName="div")})
	List links();
	
	@Filter("href.contains('automationrockstars.com')")
	@FindBy(tagName="a")
	UiObject arsLink();
	
	@FindBy(tagName="a")
	@Filter("text.contains('GitHub')")
	Link githubLink();
	
	FluentIterable<SearchResultDiv> allResults();
	
	@FindBy(id="image:glogo.png")
	@Image
	WebElement googleLogo();
}
