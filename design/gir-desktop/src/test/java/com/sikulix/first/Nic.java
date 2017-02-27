package com.sikulix.first;

import com.automationrockstars.design.desktop.driver.FindByImage;
import com.automationrockstars.design.desktop.driver.ImageUiObject;
import com.automationrockstars.gir.desktop.ExtendedUiPart;

@FindByImage("fulljson.png")
public interface Nic extends ExtendedUiPart{

	@FindByImage("extras.png")
	ImageUiObject extras();
	
	@FindByImage("dd.png")
	ImageUiObject dd();
}
