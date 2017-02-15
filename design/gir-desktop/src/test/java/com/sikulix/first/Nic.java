package com.sikulix.first;

import com.automationrockstars.gir.desktop.ExtendedUiPart;
import com.automationrockstars.gir.desktop.FindByImage;
import com.automationrockstars.gir.desktop.ImageUiObject;

@FindByImage("c:/tmp/fulljson.png")
public interface Nic extends ExtendedUiPart{

	@FindByImage("c:/tmp/extras.png")
	ImageUiObject extras();
	
	@FindByImage("c:/tmp/dd.png")
	ImageUiObject dd();
}
