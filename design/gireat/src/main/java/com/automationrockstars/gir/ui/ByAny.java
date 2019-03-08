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

import com.automationrockstars.design.gir.webdriver.FilterableSearchContext;
import com.google.common.collect.Lists;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class ByAny extends org.openqa.selenium.By {

    private final org.openqa.selenium.By[] bys;

    public ByAny(org.openqa.selenium.By... bys) {
        this.bys = bys;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        List<WebElement> result = Lists.newArrayList();
        for (org.openqa.selenium.By by : bys) {
            try {
                result.addAll(context.findElements(by));
                if (context instanceof FilterableSearchContext) {
                    break;
                }
            } catch (Exception ignore) {
            }
        }
        if (result == null || result.size() == 0) {
            throw new NoSuchElementException("Element identifies by " + this + " not found");
        }
        return result;
    }

    public String toString() {
        return String.format("any of locators %s", Arrays.toString(bys));
    }

}
