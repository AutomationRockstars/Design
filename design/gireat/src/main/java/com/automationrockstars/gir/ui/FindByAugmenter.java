package com.automationrockstars.gir.ui;

import com.automationrockstars.gir.ui.part.FindByAugmenters;

/**
 *  Interface to be used for translating FindBy into By programmatically
 *
 */
public interface FindByAugmenter {

	
	/**
	 * This method allows to change value retrieved from FindBy and construct By. Use {{@link FindByAugmenters} to help operating on FindBy
	 * @param parent UiPart that is parent of the element
	 * @param toBeAugmented FindBy containing value to be augmented
	 * @return By constructed from changed FindBy
	 */
	org.openqa.selenium.By augment(Class<? extends UiPart> parent, FindBy toBeAugmented); 
}
