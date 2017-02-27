package com.automationrockstars.design.desktop.driver;

import java.util.Iterator;

import org.openqa.selenium.SearchContext;

public interface ImageSearchContext extends SearchContext {

	ImageUiObject findElement(ByImage by);
	ImageUiObject findElement(String imagePath);
	
	
	Iterator<ImageUiObject> findElements(ByImage by);
	Iterator<ImageUiObject> findElements(String imagePath);
}
