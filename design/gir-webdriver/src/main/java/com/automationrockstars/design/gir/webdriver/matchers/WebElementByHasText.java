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
package com.automationrockstars.design.gir.webdriver.matchers;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.StringContains;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

public class WebElementByHasText extends TypeSafeMatcher<By> {

    private final String textToFind;
    String foundText;
    private Exception byToTextException;

    public WebElementByHasText(String textToFind) {
        this.textToFind = textToFind;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(textToFind);

    }

    protected void describeMismatchSafely(By item, Description mismatchDescription) {
        if (byToTextException != null) {
            if (byToTextException instanceof NoSuchElementException || byToTextException instanceof TimeoutException) {
                mismatchDescription.appendText(String.format("element %s cannot be found", item));
            } else {
                mismatchDescription.appendText(String.format("Exception %s occured during search for element %s", byToTextException, item));
            }
        } else {
            mismatchDescription.appendText(foundText);
        }

    }

    ;

    @Override
    protected boolean matchesSafely(By item) {
        try {
            foundText = DriverFactory.getDriver().findElement(item).getText();
            return new StringContains(textToFind).matchesSafely(foundText);
        } catch (Exception e) {
            this.byToTextException = e;
            return false;
        }
    }

}
