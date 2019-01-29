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

import com.automationrockstars.design.gir.webdriver.HasLocator;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.collect.FluentIterable;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.FluentWait;
import ru.yandex.qatools.htmlelements.element.Link;

public abstract interface UiPart extends WrapsElement, WebElement, HasLocator {

    String name();

    FluentIterable<UiObject> children();

    boolean has(org.openqa.selenium.By element);

    boolean hasText(String text);

    boolean isPresent();

    boolean isVisible();

    FluentWait<SearchContext> delay();

    void waitForHidden();

    UiObject childWithText(String text);

    Link childLinkWithText(String text);

    SearchContext parent();

}
