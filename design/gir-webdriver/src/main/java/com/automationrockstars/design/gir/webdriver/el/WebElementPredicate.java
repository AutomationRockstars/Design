package com.automationrockstars.design.gir.webdriver.el;

import org.mvel2.MVEL;
import org.openqa.selenium.WebElement;

import com.google.common.base.Predicate;

public class WebElementPredicate implements Predicate<WebElement> {
	private final Object filter;
	private final String predicate;
	public WebElementPredicate(String predicate){
		this.filter = MVEL.compileExpression(predicate);	
		this.predicate = predicate;
	}



	public static boolean check(WebElement element, String predicate){
		return new WebElementPredicate(predicate).apply(element);
	}

	public String toString(){
		return String.format("filter %s", predicate);
	}

	@Override
	public boolean apply(WebElement item) {
		try {
			return (Boolean) MVEL.executeExpression(filter, new WebElementVariableFactory(item));
		} catch(Throwable t){
			return false;
		}
	}
}
