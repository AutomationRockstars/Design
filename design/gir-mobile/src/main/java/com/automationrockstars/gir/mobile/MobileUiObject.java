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
package com.automationrockstars.gir.mobile;

import com.automationrockstars.design.gir.webdriver.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.Map;
import java.util.Objects;

import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectActionPluginService.actionPlugins;
import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectFindPluginService.findPlugins;
import static com.automationrockstars.design.gir.webdriver.plugin.UiObjectInfoPluginService.infoPlugins;

public class MobileUiObject extends UiObject implements HasWaits, CanSwipe, CanVerify, HasLocator {

    private Map<String, By> byLocators;

    public MobileUiObject(String platform, By by) {
        setLocator(platform, by);
    }

    public MobileUiObject(String platform, String... filter) {
        setFilter(platform, filter);
    }

    public MobileUiObject(WebElement element, By locator, String name) {
        this(element, locator);
        setName(name);
    }

    public MobileUiObject() {
    }

    MobileUiObject(WebElement toWrap, By by) {
        super(toWrap, by);
        setLocator(MobileFactory.currentPlatform(), by);

    }

    Map<String, By> getLocators() {
        if (byLocators == null) {
            byLocators = Maps.newHashMap();
        }
        return byLocators;
    }

    public void setFilter(String platform, String... filterParts) {
        if (MobileFactory.ANDROID.equals(platform)) {
            setLocator(platform, new ByVisibleTextAndroid(filterParts));
        } else {
            setLocator(platform, new ByVisibleTextIos(filterParts));
        }
    }

    public void setLocator(String platform, By by) {
        getLocators().put(platform, by);
    }

    public void waitForPresent() {
        findPlugins().beforeWaitForPresent(this);
        By locator = getLocator();
        Preconditions.checkNotNull(locator, "There is not locator for platform {} to identify object", MobileFactory.currentPlatform());
        MobileSearchUtils.waitForPresent(locator);
        findPlugins().afterWaitForPresent(this);
    }

    public void waitForVisible() {
        findPlugins().beforeWaitForVisible(this);
        By locator = getLocator();
        Preconditions.checkNotNull(locator, "There is not locator for platform {} to identify object", MobileFactory.currentPlatform());
        MobileSearchUtils.waitForVisible(locator);
        findPlugins().afterWaitForVisible(this);
    }

    public void click() {
        actionPlugins().beforeClick(this);
        MobileUtils.pressAdjusted(getWrappedElement());
        actionPlugins().afterClick(this);
    }

    public void sendKeys(CharSequence... keys) {
        click();
        super.sendKeys(keys);
    }

    @Override
    public WebElement getWrappedElement() {
        if (isWrongPlatform()) return new NullObject();
        if (wrapped == null) {
            By locator = getLocator();
            findPlugins().beforeFindElement(null, locator);
            wrapped = MobileSearchUtils.waitForVisible(locator);
            findPlugins().afterFindElement(null, locator, wrapped);
        }
        return wrapped;
    }

    public boolean isPresent() {
        By locator = getLocator();
        if (locator == null) return false;
        return MobileSearchUtils.isPresent(locator);
    }

    public boolean isDisplayed() {
        infoPlugins().beforeIsDisplayed(this);
        By locator = getLocator();
        boolean value = false;
        if (locator != null) {
            value = MobileSearchUtils.isVisible(locator);
        }
        infoPlugins().afterIsDisplayed(this, value);
        return value;
    }

    private boolean isWrongPlatform() {
        return getLocator() == null;
    }

    public void swipeLeft() {
        MobileUtils.swipe(getWrappedElement(), 80, 0);
    }

    @Override
    public void swipeRight() {
        MobileUtils.swipe(getWrappedElement(), -80, 0);

    }

    @Override
    public void swipeUp() {
        MobileUtils.swipe(getWrappedElement(), 0, -80);

    }

    @Override
    public void swipeDown() {
        MobileUtils.swipe(getWrappedElement(), 0, 80);
    }

    public By getLocator() {
        return getLocators().get(MobileFactory.currentPlatform());
    }

    public String toString() {
        if (getName() == null) {
            setName(Objects.toString(getLocator()));
        }
        return super.toString();
    }

    @Override
    public void verifyVisible() {
        verifyVisible(String.format("Object %s not visible", toString()));

    }

    @Override
    public void verifyVisible(String failMessage) {
        try {
            waitForVisible();
        } catch (Exception e) {
            throw new AssertionError(failMessage);
        }

    }

    @Override
    public void verifyVisible(String failMessage, HasLocator failObject) {
        verifyVisible(failMessage, failObject.getLocator());
    }

    @Override
    public void verifyVisible(String failMessage, By errorObject) {
        try {
            UiObject e = MobileSearchUtils.waitForAnyVisible(getLocator(), errorObject);
            if (e.getLocator() == errorObject) {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new AssertionError(failMessage);
        }
    }

    public boolean exists() {
        try {
            MobileSearchUtils.findVisible(getLocator());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean checkedClick() {
        actionPlugins().beforeClick(this);
        boolean result = false;
        try {
            Waits.waitUntilClickable(getWrappedElement());
            click();
            result = true;
            actionPlugins().afterClick(this);
        } catch (WebDriverException e) {
        }
        return result;
    }

    public MobileUiObject getParent() {
        return (MobileUiObject) getWrappedElement().findElement(By.xpath("//element/.."));
    }

    public void tap() {
        MobileUtils.tap(getWrappedElement());
    }


}
