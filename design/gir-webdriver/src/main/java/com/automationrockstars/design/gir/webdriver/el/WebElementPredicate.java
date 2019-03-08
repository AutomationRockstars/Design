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

package com.automationrockstars.design.gir.webdriver.el;

import com.google.common.base.Predicate;
import org.mvel2.MVEL;
import org.openqa.selenium.WebElement;

public class WebElementPredicate implements Predicate<WebElement> {
    private final Object filter;
    private final String predicate;

    public WebElementPredicate(String predicate) {
        this.filter = MVEL.compileExpression(predicate);
        this.predicate = predicate;
    }


    public static boolean check(WebElement element, String predicate) {
        return new WebElementPredicate(predicate).apply(element);
    }

    public String toString() {
        return String.format("filter %s", predicate);
    }

    @Override
    public boolean apply(WebElement item) {
        try {
            return (Boolean) MVEL.executeExpression(filter, new WebElementVariableFactory(item));
        } catch (Throwable t) {
            return false;
        }
    }
}
