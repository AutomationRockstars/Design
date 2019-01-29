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

import org.openqa.selenium.By;

import static com.automationrockstars.gir.ui.part.FindByAugmenters.instanceForValue;
import static com.automationrockstars.gir.ui.part.FindByAugmenters.using;

public class TestFixingAugmenter implements FindByAugmenter {

    @Override
    public By augment(Class<? extends UiPart> parent, FindBy toBeAugmented) {
        return instanceForValue(toBeAugmented, using(toBeAugmented).replace("removeme", ""));
    }


}
