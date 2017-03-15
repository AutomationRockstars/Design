package com.automationrockstars.gir.ui;

import static com.automationrockstars.gir.ui.part.FindByAugmenters.instanceForValue;
import static com.automationrockstars.gir.ui.part.FindByAugmenters.using;

import org.openqa.selenium.By;
public class TestFixingAugmenter implements FindByAugmenter{

	@Override
	public By augment(Class<? extends UiPart> parent, FindBy toBeAugmented) {
			return instanceForValue(toBeAugmented, using(toBeAugmented).replace("removeme", ""));
	}
	
	

}
