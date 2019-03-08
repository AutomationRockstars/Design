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

import com.google.common.collect.FluentIterable;
import org.openqa.selenium.WebElement;

@FindBy(tagName = "head")
@Covered(lookForVisibleParent = false)
public interface Head extends UiPart {

    @FindBy(tagName = "meta")
    @Covered(lookForVisibleParent = false)
    FluentIterable<WebElement> meta();

    @FindBy(tagName = "script")
    @Covered(lookForVisibleParent = false)
    FluentIterable<WebElement> script();

    @FindBy(tagName = "style")
    @Covered(lookForVisibleParent = false)
    FluentIterable<WebElement> style();

    @FindBy(tagName = "link")
    @Covered(lookForVisibleParent = false)
    FluentIterable<WebElement> link();

}
