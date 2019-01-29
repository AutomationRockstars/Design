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

import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.element.Link;

@WithFindByAugmenter(TestFixingAugmenter.class)
@FindBy(className = "removemeg")
public interface SearchResultDiv extends UiPart {

    @WithFindByAugmenter(TestFixingAugmenter.class)
    @FindBy(tagName = "a")
    Link link();

    @WithFindByAugmenter(TestFixingAugmenter.class)
    @FindBy(className = "removemes")
    WebElement description();

}
