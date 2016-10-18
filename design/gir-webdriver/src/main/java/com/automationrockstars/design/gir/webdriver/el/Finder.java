package com.automationrockstars.design.gir.webdriver.el;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ByIdOrName;

import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class Finder {


	public WebElement by(String findString){
		return DriverFactory.getDriver().findElement(stringToBy(findString));
	}
	public static final String CLASS_NAME= "className";
	public static final String CSS= "css";
	public static final String ID= "id";
	public static final String ID_OR_NAME= "idOrName";
	public static final String LINK_TEXT= "linkText";
	public static final String NAME= "name";
	public static final String PARTIAL_LINK_TEXT= "partialLinkText";
	public static final String TAG_NAME= "tagName";
	public static final String XPATH= "xpath";
		
	private static By stringToBy(String findString){
		final String how = findString.split("=")[0].trim();
		
		String using =  findString.split("=")[1].trim();
		if (using.charAt(0)=='\'' || using.charAt(0) == '"'){
			using = using.substring(1, using.length()-2);
		}
		switch (how) {
	      case CLASS_NAME:
	        return By.className(using);

	      case CSS:
	        return By.cssSelector(using);

	      case ID:
	        return By.id(using);

	      case ID_OR_NAME:
	        return new ByIdOrName(using);

	      case LINK_TEXT:
	        return By.linkText(using);

	      case NAME:
	        return By.name(using);

	      case PARTIAL_LINK_TEXT:
	        return By.partialLinkText(using);

	      case TAG_NAME:
	        return By.tagName(using);

	      case XPATH:
	        return By.xpath(using);

	      default:
	        // Note that this shouldn't happen (eg, the above matches all
	        // possible values for the How enum)
	        throw new IllegalArgumentException("Cannot determine how to locate element ");
	    }
		
	}

}
