package com.automationrockstars.design.gir.webdriver;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

public class ByOrder extends org.openqa.selenium.By {

	private final org.openqa.selenium.By locator;
	private final int index;
	public ByOrder(org.openqa.selenium.By locator, int index ){
		this.locator = locator;
		this.index = index;
	}
	@Override
	public List<WebElement> findElements(SearchContext context) {
		List<WebElement> all = context.findElements(locator);
		if (all.size() <= index){
			throw new NoSuchElementException("Cannot find " + index + " elements using " + locator);
		}
		return Lists.newArrayList(all.get(index));
	}
	
	@Override
	public WebElement findElement(SearchContext context){
		if (index == 0){
			return context.findElement(locator);
		} else {
			return findElements(context).get(0);
			
		}
	}
	@Override
	public String toString(){
		return String.format("%s of %s ", index,locator);
	}

}
