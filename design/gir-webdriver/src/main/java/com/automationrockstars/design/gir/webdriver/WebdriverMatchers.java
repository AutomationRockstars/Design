/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.design.gir.webdriver;

import com.automationrockstars.design.gir.webdriver.matchers.WebElementByHasText;
import com.automationrockstars.design.gir.webdriver.matchers.WebElementHasText;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class WebdriverMatchers {

    public static Matcher<WebElement> hasText(String text) {
        return new WebElementHasText(text);
    }

    public static Matcher<By> foundAndHasText(String text) {
        return new WebElementByHasText(text);
    }

    public static boolean pageContainsText(String text) {
        return new WebElementByHasText(text).matches(By.tagName("body"));
    }

    public static boolean pageDoesntContainText(String text) {
        return !pageContainsText(text);
    }

    public static boolean pageHasTitle(String text) {
        return new StringContains(text).matchesSafely(DriverFactory.getDriver().getTitle());
    }

    public static boolean elementDoesntExist(By element) {
        return !elementExists(element);
    }

    public static boolean sourceContainsText(String text) {
        return new StringContains(text).matchesSafely(DriverFactory.getDriver().getPageSource());
    }

    public static boolean elementExists(By element) {
        try {
            DriverFactory.getDriver().findElement(element);
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

}
