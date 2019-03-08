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

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import org.openqa.selenium.*;
import org.openqa.selenium.internal.WrapsElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.automationrockstars.design.gir.webdriver.DriverFactory.getDriver;
import static com.automationrockstars.design.gir.webdriver.DriverFactory.getUnwrappedDriver;

public class Page {

    private static final Logger LOG = LoggerFactory.getLogger(Page.class);

    public static boolean hasElement(By by) {
        return isElementPresent(by);
    }

    public static boolean isElementPresent(By by) {
        try {
            LOG.info("Checking if element {} is currently present", by);
            WebElement a = getUnwrappedDriver().findElement(by);
            LOG.info("Element {} present using {}", by, a);
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }


    public static boolean isElementVisible(By by) {
        LOG.debug("Checking if element {} is currently visible", by);
        return Waits.visible(by).apply(getUnwrappedDriver()) != null;
    }

    public static WebElement waitForElement(By by) {
        LOG.info("Looking for element {} on page", by);
        return getDriver().findElement(by);
    }

    public static String title() {
        return getDriver().getTitle();
    }

    public static String url() {
        return getDriver().getCurrentUrl();
    }

    public static String source() {
        return getDriver().getPageSource();
    }

    public static WebElement waitForVisible(Class<? extends UiFragment> uiFragment) {
        return Waits.withDefaultDelay().until(Waits.visible(UiFragment.getLocator(uiFragment)));
    }

    public static void waitForHidden(Class<? extends UiFragment> uiFragment) {
        Waits.webElementWait(getUnwrappedDriver()).until(Waits.hidden(UiFragment.getLocator(uiFragment)));
    }

    public static void scrollTo(WebElement element) {
        if (WrapsElement.class.isAssignableFrom(element.getClass())) {
            element = ((WrapsElement) element).getWrappedElement();
        }
        ((JavascriptExecutor) DriverFactory.getDriver()).executeScript("arguments[0].scrollIntoView();"
                , element);
    }

    public static void scrollUp() {
        ((JavascriptExecutor) DriverFactory.getDriver()).executeScript("window.scrollBy(0, -500)", "");
    }

    public static void scrollDown() {
        ((JavascriptExecutor) DriverFactory.getDriver()).executeScript("window.scrollBy(0, 500)", "");
    }


    public static WebElement elementWithText(Iterable<?> elements, final String text) {
        Preconditions.checkArgument(elements.iterator().hasNext(), "Elements cannot be empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(text), "Searched text cannot be empty");
        final Class<?> elementType = elements.iterator().next().getClass();
        Preconditions.checkArgument(WebElement.class.isAssignableFrom(elementType)
                || WrapsElement.class.isAssignableFrom(elementType), "Class %s is not supported", elementType);
        Iterable<WebElement> webElements = Iterables.transform(elements, new Function<Object, WebElement>() {
            @Override
            public WebElement apply(Object input) {
                if (WrapsElement.class.isAssignableFrom(elementType)) {
                    return ((WrapsElement) input).getWrappedElement();
                } else {
                    return (WebElement) input;
                }
            }
        });

        Optional<WebElement> elementWithText = Iterables.tryFind(webElements, new Predicate<WebElement>() {
            @Override
            public boolean apply(WebElement input) {
                return input.getText().contains(text);
            }

        });
        if (!elementWithText.isPresent()) {
            throw new NoSuchElementException(String.format("Element with text %s is not on withing %s", text, elements));
        }
        return elementWithText.get();
    }


}
