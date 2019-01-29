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

import com.automationrockstars.design.gir.webdriver.el.WebElementPredicate;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class FilteredBy extends org.openqa.selenium.By {

    private final Predicate<WebElement> predicate;
    private final org.openqa.selenium.By locator;

    public FilteredBy(org.openqa.selenium.By locator, String filter) {
        this.predicate = new WebElementPredicate(filter);
        this.locator = locator;
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        return FluentIterable.from(locator.findElements(context))
                .filter(predicate).toList();
    }

    public WebElement findElement(SearchContext context) {
        com.google.common.base.Optional<WebElement> result = FluentIterable.from(locator.findElements(context))
                .firstMatch(predicate);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new NoSuchElementException("Element identified by " + locator + " and " + predicate + " not found in " + context);
        }

    }

    public String toString() {
        return String.format("by %s using filter %s", locator, predicate);
    }

}
