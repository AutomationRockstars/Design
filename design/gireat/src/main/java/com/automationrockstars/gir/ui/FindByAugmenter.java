/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

package com.automationrockstars.gir.ui;

import com.automationrockstars.gir.ui.part.FindByAugmenters;

/**
 * Interface to be used for translating FindBy into By programmatically
 */
public interface FindByAugmenter {


    /**
     * This method allows to change value retrieved from FindBy and construct By. Use {{@link FindByAugmenters} to help operating on FindBy
     *
     * @param parent        UiPart that is parent of the element
     * @param toBeAugmented FindBy containing value to be augmented
     * @return By constructed from changed FindBy
     */
    org.openqa.selenium.By augment(Class<? extends UiPart> parent, FindBy toBeAugmented);
}
