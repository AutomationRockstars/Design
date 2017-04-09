package com.automationrockstars.gir.ui;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.automationrockstars.design.gir.webdriver.el.WebElementPredicate;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class FilteredBy extends org.openqa.selenium.By{

	private final Predicate<WebElement> predicate;
	private final org.openqa.selenium.By locator;
	public FilteredBy(org.openqa.selenium.By locator, String filter) {	
		this.predicate = new WebElementPredicate(filter);	
		this.locator = locator;
	}
	@Override
	public List<WebElement> findElements(SearchContext context) {
		return FluentIterable.from(locator.findElements(context))
				.filter( predicate).toList();
	}
	
	public WebElement findElement(SearchContext context){
		com.google.common.base.Optional<WebElement> result = FluentIterable.from(locator.findElements(context))
				.firstMatch(predicate);
		if (result.isPresent()){
			return result.get();
		} else {
			throw new NoSuchElementException("Element identified by " + locator + " and " + predicate + " not found in " + context);
		}
		
	}
	
	public String toString(){
		return String.format("by %s using filter %s", locator,predicate);
	}

}
