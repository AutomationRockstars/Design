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

package com.automationrockstars.gir.ui.part;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.collect.Lists;
import org.openqa.selenium.*;
import org.openqa.selenium.internal.WrapsElement;

import java.util.List;

import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectFindPluginService.findPlugins;

public class EmptyUiObject extends UiObject {

    private final EmptyWebElement wrapped = new EmptyWebElement();

    public static boolean isEmpty(WebElement element) {
        if (element instanceof EmptyUiObject || element instanceof EmptyWebElement) {
            return true;
        } else if (WrapsElement.class.isAssignableFrom(element.getClass())) {
            return isEmpty(((WrapsElement) element).getWrappedElement());
        } else {
            return false;
        }
    }

    public List<WebElement> findElements(By by) {
        findPlugins().beforeFindElements(this, by);
        List<WebElement> found = getWrappedElement().findElements(by);
        findPlugins().afterFindElements(this, by, found);
        return wrapAll(found, getLocator(), by);
    }

    public WebElement findElement(By by) {
        findPlugins().beforeFindElement(this, by);
        WebElement result = getWrappedElement().findElement(by);
        findPlugins().afterFindElement(this, by, result);
        return wrap(result, by);
    }

    public EmptyUiObject withTagName(String tagName) {

        wrapped.tagName = tagName;
        return this;
    }

    @Override
    public WebElement getWrappedElement() {
        return wrapped;
    }

    public String getName() {
        return "Empty Ui Object";
    }

    static class EmptyWebElement implements WebElement {
        private String tagName = "none";

        @Override
        public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
            return target.convertFromBase64Png("");
        }

        @Override
        public void click() {
        }

        @Override
        public void submit() {
        }

        @Override
        public void sendKeys(CharSequence... keysToSend) {
        }

        @Override
        public void clear() {
        }

        @Override
        public String getTagName() {
            return tagName;
        }

        @Override
        public String getAttribute(String name) {
            return "";
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public List<WebElement> findElements(By by) {
            return Lists.newArrayList((WebElement) new EmptyUiObject());
        }

        @Override
        public WebElement findElement(By by) {
            return new EmptyUiObject();
        }

        @Override
        public boolean isDisplayed() {
            return true;
        }

        @Override
        public Point getLocation() {
            return new Point(0, 0);
        }

        @Override
        public Dimension getSize() {
            return new Dimension(0, 0);
        }

        @Override
        public Rectangle getRect() {
            return new Rectangle(0, 0, 0, 0);
        }

        @Override
        public String getCssValue(String propertyName) {
            return "";
        }
    }

}
