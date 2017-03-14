package com.automationrockstars.gir.ui;

public interface FindByAugmenter {

	org.openqa.selenium.By augment(Class<? extends UiPart> parent, FindBy toBeAugmented); 
}
