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

package com.automationrockstars.design.desktop.driver;

import org.openqa.selenium.SearchContext;

import java.util.Iterator;

public interface ImageSearchContext extends SearchContext {

    ImageUiObject findElement(ByImage by);

    ImageUiObject findElement(String imagePath);


    Iterator<ImageUiObject> findElements(ByImage by);

    Iterator<ImageUiObject> findElements(String imagePath);
}
