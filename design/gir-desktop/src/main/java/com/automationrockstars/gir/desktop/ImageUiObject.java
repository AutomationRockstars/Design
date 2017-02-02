package com.automationrockstars.gir.desktop;

import java.nio.file.Path;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.internal.Locatable;

public interface ImageUiObject  extends HasInputDevices,Locatable, WebElement, ImageSearchContext{

	
	public void click();
	
	public void sendKeys(CharSequence... keys);
	
	public void waitUntilVisible();
	
	public void waitUntilHidden();
	
	public String getText();
	
	public String getName();
	
	public Path getLocator();
	
	public Point getLocation();
	
	public Dimension getSize();
	
	public boolean isVisible();

}
