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

package com.automationrockstars.design.gir.webdriver;

import com.google.common.collect.Lists;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ByOrder extends org.openqa.selenium.By {

    private final org.openqa.selenium.By locator;
    private final int index;

    public ByOrder(org.openqa.selenium.By locator, int index) {
        this.locator = locator;
        this.index = index;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        List<WebElement> all = context.findElements(locator);
        if (all.size() <= index) {
            throw new NoSuchElementException("Cannot find " + index + " elements using " + locator);
        }
        return Lists.newArrayList(all.get(index));
    }

    @Override
    public WebElement findElement(SearchContext context) {
        if (index == 0) {
            return context.findElement(locator);
        } else {
            return findElements(context).get(0);

        }
    }

    @Override
    public String toString() {
        return String.format("%s of %s ", index, locator);
    }

}
