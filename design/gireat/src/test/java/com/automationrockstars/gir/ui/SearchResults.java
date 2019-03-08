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

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.context.Image;
import com.google.common.collect.FluentIterable;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.How;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

@WithFindByAugmenter(TestFixingAugmenter.class)
@org.openqa.selenium.support.FindBy(id = "removemeres")
@Name("Search Results")
public interface SearchResults extends UiPart {

    @FindBy(how = How.CLASS_NAME, using = "g")
    FluentIterable<WebElement> results();

    @SuppressWarnings("rawtypes")
    @WithFindByAugmenter(TestFixingAugmenter.class)
    @FindAll({
            @FindBy(tagName = "removemea"),
            @FindBy(tagName = "div")})
    List links();

    @Filter("href.contains('automationrockstars.com')")
    @FindBy(tagName = "a")
    UiObject arsLink();


    @FindBy(tagName = "a")
    @Filter("text.contains('GitHub')")
    Link githubLink();

    FluentIterable<SearchResultDiv> allResults();

    @FindBy(id = "image:glogo.png")
    @Image
    WebElement googleLogo();
}
