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

import org.openqa.selenium.WebDriver;

public interface UiDriverPlugin {

    void beforeInstantiateDriver();

    void afterInstantiateDriver(WebDriver driver);

    void beforeGetDriver();

    void afterGetDriver(WebDriver driver);

    void beforeCloseDriver(WebDriver driver);

    void afterCloseDriver();


}
