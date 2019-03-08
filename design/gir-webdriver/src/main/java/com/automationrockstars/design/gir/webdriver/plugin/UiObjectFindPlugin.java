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
package com.automationrockstars.design.gir.webdriver.plugin;

import com.automationrockstars.design.gir.webdriver.UiObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface UiObjectFindPlugin {

    void beforeFindElements(UiObject element, By by);

    void afterFindElements(UiObject element, By by, List<WebElement> result);

    void beforeFindElement(UiObject element, By by);

    void afterFindElement(UiObject element, By by, WebElement result);

    void beforeWaitForVisible(UiObject element);

    void afterWaitForVisible(UiObject element);

    void beforeWaitForPresent(UiObject element);

    void afterWaitForPresent(UiObject element);
}
