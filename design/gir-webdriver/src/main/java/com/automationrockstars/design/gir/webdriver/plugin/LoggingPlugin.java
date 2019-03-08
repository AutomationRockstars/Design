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
package com.automationrockstars.design.gir.webdriver.plugin;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.UiObject;
import org.openqa.selenium.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.interactions.Coordinates;
import java.util.List;


public class LoggingPlugin implements UiObjectInfoPlugin, UiObjectActionPlugin, UiObjectFindPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingPlugin.class);

    @Override
    public void beforeGetTagName(UiObject element) {
        LOG.info("Getting tag name of {}", element);

    }

    @Override
    public void afterGetTagName(UiObject element, String value) {
        LOG.info("Tag name for {} is {}", element, value);

    }

    @Override
    public void beforeGetAttribute(UiObject element, String name) {
        LOG.info("Getting attribute {} of element {}", name, element);

    }

    @Override
    public void afterGetAttribute(UiObject element, String name, String value) {
        LOG.info("Got value {} of attribute {} for {}", value, name, element);

    }

    @Override
    public void beforeIsSelected(UiObject element) {
        LOG.info("Checking if {} is selected", element);

    }

    @Override
    public void afterIsSelected(UiObject element, boolean value) {
        LOG.info("Element {} is selected {}", element, value);

    }

    @Override
    public void beforeIsEnabled(UiObject element) {
        LOG.info("Checking if {} is enabled", element);

    }

    @Override
    public void afterIsEnabled(UiObject element, boolean value) {
        LOG.info("Element {} is enabled {}", element, value);

    }

    @Override
    public void beforeGetText(UiObject element) {
        LOG.info("Getting text of {}", element);

    }

    @Override
    public void afterGetText(UiObject element, String value) {
        LOG.info("Element {} has text {}", element, value);

    }

    @Override
    public void beforeIsDisplayed(UiObject element) {
        LOG.info("Checking if element {} is displayed", element);

    }

    @Override
    public void afterIsDisplayed(UiObject element, boolean value) {
        LOG.info("Element {} is displayed {}", element, value);
    }

    @Override
    public void beforeGetLocation(UiObject element) {
        LOG.info("Getting location of {}", element);
    }

    @Override
    public void afterGetLocation(UiObject element, Point value) {
        LOG.info("Element {} has location {}", element, value);
    }

    @Override
    public void beforeGetSize(UiObject element) {
        LOG.info("Getting size of element {}", element);
    }

    @Override
    public void afterGetSize(UiObject element, Dimension value) {
        LOG.info("Elment {} has size {}", element, value);

    }

    @Override
    public void beforeGetCssValue(UiObject element, String propertyName) {
        LOG.info("Getting CSS value {} of element {}", propertyName, element);
    }

    @Override
    public void afterGetCssValue(UiObject element, String propertyName, String value) {
        LOG.info("Element {} has CSS {}:{}", element, propertyName, value);

    }

    @Override
    public <X> void beforeGetScreenshotAs(UiObject element, OutputType<X> target) {
        LOG.info("Getting screnshot of {} to {}", element, target);

    }

    @Override
    public <X> void afterGetScreenshotAs(UiObject element, OutputType<X> target, X screenshot) {
        LOG.info("Screenshot {} of {} taken to {}", screenshot, element, target);

    }

    @Override
    public void beforeGetCoordinates(UiObject uiObject) {
        LOG.info("Getting coordindate of {}", uiObject);
    }

     @Override
    public void afterGetCoordinates(UiObject uiObject, Coordinates result) {
        LOG.info("Element {} has coordinates {} on page", uiObject, result.onPage());

    }

    @Override
    public void beforeFindElements(UiObject element, By by) {
        LOG.info("Starting to find elements {} using {}", by, element);
    }

    @Override
    public void afterFindElements(UiObject element, By by, List<WebElement> result) {
        LOG.info("{} elements found with {} using {}", result.size(), by, element);

    }

    @Override
    public void beforeFindElement(UiObject element, By by) {
        LOG.info("Starting to search of element located by {} using {}", by, element);

    }

    @Override
    public void afterFindElement(UiObject element, By by, WebElement result) {
        LOG.info("Found {} with locator {} using {}", result, by, element);

    }

    @Override
    public void beforeClick(UiObject element) {
        LOG.info("Clicking on {}", element);

    }

    @Override
    public void afterClick(UiObject element) {
        LOG.info("Element {} clicked", element);

    }

    @Override
    public void beforeSubmit(UiObject element) {
        LOG.info("Submitting on {}", element);

    }

    @Override
    public void afterSubmit(UiObject element) {
        LOG.info("Element {} submitted", element);

    }

    private CharSequence[] hideKeys(final UiObject element, final CharSequence... keys) {
        if (ConfigLoader.config().containsKey("webdriver.log.hide_inputs")) {
            String[] inputsToHide = ConfigLoader.config().getStringArray("webdriver.log.hide_inputs");
            for (String elementName : inputsToHide) {
                if (element.toString().toLowerCase().contains(elementName.toLowerCase())) {
                    return new CharSequence[]{"**************"};
                }
            }
            return keys;
        } else return keys;
    }

    @Override
    public void beforeSendKeys(UiObject element, CharSequence... keysToSend) {
        LOG.info("Sending {} to element {}", hideKeys(element, keysToSend), element);

    }

    @Override
    public void afterSendKeys(UiObject element, CharSequence... keysToSend) {
        LOG.info("Keys {} sent to {}", hideKeys(element, keysToSend), element);

    }

    @Override
    public void beforeClear(UiObject element) {
        LOG.info("Starting to clear {}", element);

    }

    @Override
    public void afterClear(UiObject element) {
        LOG.info("Element {} cleared", element);

    }

    @Override
    public void beforeWaitForVisible(UiObject element) {
        LOG.info("Waiting for {} being visible", element);

    }

    @Override
    public void afterWaitForVisible(UiObject element) {
        LOG.info("Waiting for visibility of {} finished ", element);

    }

    @Override
    public void beforeWaitForPresent(UiObject element) {
        LOG.info("Waiting for {} being present", element);

    }

    @Override
    public void afterWaitForPresent(UiObject element) {
        LOG.info("Waiting for presence of {} finished", element);

    }

    @Override
    public void beforeGetRect(UiObject uiObject) {
        LOG.info("Getting rectangel of {}", uiObject);

    }

    @Override
    public void afterGetRect(UiObject uiObject, Rectangle rect) {
        LOG.info("Rectangle of {} is {}", uiObject, rect);

    }

}
