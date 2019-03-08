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

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.Waits;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import org.openqa.selenium.*;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static com.automationrockstars.gir.mobile.ByVisibleText.visibleText;
import static com.automationrockstars.gir.mobile.MobileFactory.DEFAULT_DELAY;
import static com.automationrockstars.gir.mobile.MobileFactory.DEFAULT_POLL;

public class MobileUtils {


    private static final Logger LOG = LoggerFactory.getLogger(MobileUtils.class);
    private static final int pressWait = ConfigLoader.config().getInt("mobile.press.wait", 2500);
    static Predicate<WebElement> displayed = new Predicate<WebElement>() {

        @Override
        public boolean apply(WebElement arg0) {
            LOG.debug("{} is displayed {}", arg0, arg0.isDisplayed());
            return arg0.isDisplayed();
        }

    };

    public static void scrollDown() {
        executeScroll(Collections.singletonMap("direction", "down"));
    }

    private static void executeScroll(Map<String, String> attrs) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) MobileFactory.getDriver();
            js.executeScript("mobile: scroll", attrs);
            LOG.info("Scrolling completed");
        } catch (WebDriverException e) {
            LOG.warn("Cannot scroll {}", attrs);
        }
    }

    public static void scrollUp() {
        executeScroll(Collections.singletonMap("direction", "up"));
    }

    public static void swipe(WebElement element, int xoffsetPersents, int yoffsetPersent) {

        int startX = element.getLocation().x;
        int startY = element.getLocation().y;
        int stopX = element.getLocation().x;
        int stopY = element.getLocation().y;
        if (xoffsetPersents > 0) {
            stopX += element.getSize().getWidth() * Math.abs(xoffsetPersents) / 100;
        } else if (xoffsetPersents < 0) {
            startX += element.getSize().getWidth() * Math.abs(xoffsetPersents) / 100;
        }
        if (yoffsetPersent > 0) {
            stopY += element.getSize().getHeight() * Math.abs(yoffsetPersent) / 100;
        }
        if (yoffsetPersent < 0) {
            startY += element.getSize().getHeight() * Math.abs(yoffsetPersent) / 100;
        }

        int duration = ConfigLoader.config().getInt("mobile.swipe.time", 500);
        boolean retry = true;
        while (retry) {
            try {
                MobileFactory.getDriver().swipe(startX, startY, stopX, stopY, duration);
                retry = false;
            } catch (WebDriverException e) {

            }
        }
    }

    public static void press(int x, int y) {
        try {
            LOG.debug("Touching screen at {} {}", x, y);
            new TouchAction(MobileFactory.getDriver()).press(x, y)
                    .waitAction(600)
                    .release()
                    .waitAction(pressWait)
                    .perform();
        } catch (Throwable rb) {
            LOG.warn("Touching failed {}", rb.getMessage());
        }
    }

    public static void scrollTo(WebElement element) {
        while (!RemoteWebElement.class.isAssignableFrom(element.getClass())) {
            if (WrapsElement.class.isAssignableFrom(element.getClass())) {
                element = ((WrapsElement) element).getWrappedElement();
            } else {
                throw new RuntimeException("Element is not remote");
            }
        }
        JavascriptExecutor js = (JavascriptExecutor) MobileFactory.getDriver();
        Map<String, String> scrollObject = Maps.newHashMap();
        scrollObject.put("direction", "down");
        scrollObject.put("element", ((RemoteWebElement) element).getId());
        js.executeScript("mobile: scroll", scrollObject);
        LOG.info("Scrolling down completed");
    }

    public static void scrollToText(String text) {
        WebElement elem = MobileUiObjectFactory.onAny(text);
        Preconditions.checkNotNull(elem, "Element with text %s not found", text);
        scrollTo(elem);
    }

    public static void pressAdjusted(WebElement element) {

        int[] coordinates = adjustCoordinates(element);
        press(coordinates[0], coordinates[1]);
    }

    private static int[] adjustCoordinates(final WebElement element) {
        if (!(element instanceof NullObject)) {

            int maxX = MobileFactory.getDriver().manage().window().getSize().width;
            int maxY = MobileFactory.getDriver().manage().window().getSize().height;
            int x = element.getLocation().x;
            int y = element.getLocation().y;
            if (x > maxX || x < 0) {
                x = Math.abs(maxX - Math.abs(x));
            }
            if (y > maxY) {

                LOG.info("Element {} requires scrolling", element);
                scrollTo(element);
                WebElement scrolled = element;
                if (scrolled instanceof MobileUiObject) {
                    ((MobileUiObject) scrolled).setWrappedElement(null);
                }
                x = scrolled.getLocation().x;
                y = scrolled.getLocation().y;
                LOG.info("Element refreshed after scrolling");
            }
            if (y > maxY || y < 0) {
                y = Math.abs(maxY - Math.abs(y));
            }
            int yMove = element.getSize().height / 2;
            int xMove = element.getSize().width / 3;
            x = x + xMove;
            y = y + yMove;
            return new int[]{x, y};
        } else return new int[]{0, 0};
    }

    public static void pressWithoutX(WebElement element) {
        pressAdjusted(element);
    }

    public static void press(WebElement e, int factor) {
        int x = e.getLocation().x / factor;
        int y = e.getLocation().y / factor;
        press(x, y);


    }

    public static void pressovator() {
        @SuppressWarnings("rawtypes")
        AppiumDriver driver = MobileFactory.getDriver();
        int w = driver.manage().window().getSize().getWidth();
        int h = driver.manage().window().getSize().getHeight();
        LOG.info("GOT {} {}", w, h);
        //		w = Math.min(w, 320);
        h = Math.min(h, 650);
        ConfigLoader.config().addProperty("mobile.press.wait", 4500);
        for (int i = 40; i < w - 5; i += 10) {
            for (int j = 80; j < h - 150; j += 10) {
                press(i, j);
            }
        }
    }

    public static WebDriverWait delay() {
        return delay(DEFAULT_DELAY);
    }

    public static WebDriverWait delay(int seconds) {
        return new WebDriverWait(MobileFactory.getDriver(), seconds, DEFAULT_POLL);

    }

    public static void waitForElementToHide(final int seconds, final By by) {
        delay(seconds).until(new Function<WebDriver, Boolean>() {

            @Override
            public Boolean apply(WebDriver arg0) {
                try {
                    MobileSearchUtils.findVisible(by);
                    return false;
                } catch (Throwable l) {
                    return true;
                }
            }
        });
    }

    public static String currentContent(By by) {
        String currentContent = by.toString();

        try {
            if (MobileSearchUtils.isVisible(visibleText("Delete"))) {
                LOG.warn("there is this list on ");
                MobileSearchUtils.findPresent(visibleText("Delete")).sendKeys(Keys.ESCAPE);
            }
            currentContent = MobileSearchUtils.findVisible(by).getText();
        } catch (Throwable t) {
            LOG.warn("Seems there is a autocomplete visible. {}", t.getMessage());
            try {
                if (visibleText("Delete") != null) {
                    LOG.warn("there is this list on ");
                    MobileSearchUtils.findVisible(visibleText("Delete")).sendKeys(Keys.ESCAPE);
                    currentContent = MobileSearchUtils.findVisible(by).getText();
                }
            } catch (Throwable l) {
                LOG.error("Page {}", (Joiner.on("\n").join(Splitter.on("><").split(MobileFactory.getDriver().getPageSource()))));
            }
        }
        return currentContent;
    }

    public static WebElement clearText(By by) {
        Point toPinch = null;
        try {
            while (currentContent(by).length() > 1) {
                try {
                    MobileSearchUtils.waitForAnyVisible(by).sendKeys(Keys.DELETE);
                } catch (Throwable ignore) {
                    try {
                        if (toPinch != null) {
                            MobileFactory.getDriver().tap(1, toPinch.x, toPinch.y, 200);
                        }
                    } catch (Throwable there) {
                        LOG.warn("Pinching failed", there);

                    }
                    LOG.warn("Cleaning failed once of many {}", ignore.getMessage());
                }
            }
        } catch (Throwable t) {
            LOG.warn("Cannot clear due to {}", t.getMessage());

        }
        return MobileSearchUtils.waitForVisible(by);
    }

    public static void waitForElementToDisappear(String by) {
        waitForElementToDisappear(visibleText(by));
    }

    public static void waitForElementToDisappear(By by) {
        try {
            if (!MobileSearchUtils.isPresent(by)) {
                MobileSearchUtils.waitForPresent(by);
            }
            Waits.waitUntilHidden(by);
        } catch (Throwable tooLate) {
            LOG.debug("It wasnt there but whatever");
        }

    }

    public static void tap(int x, int y) {
        MobileFactory.getDriver().tap(1, x, y, 10);
    }

    public static void tap(WebElement e) {
        int[] coordinates = adjustCoordinates(e);
        tap(coordinates[0], coordinates[1]);
    }

    @SuppressWarnings("unchecked")
    public static void hideKeyboard() {
        try {
            if (MobileFactory.isIOS()) {
                ((IOSDriver<WebElement>) MobileFactory.getDriver()).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
            } else {
                MobileFactory.getDriver().hideKeyboard();
            }
            LOG.info("Keyboard hidden");
        } catch (WebDriverException ignore) {
            LOG.info("Trying alternative method for hiding keyboard");
            new MobileUiObject(MobileFactory.IOS, "Done").checkedClick();

        }
    }

    @Deprecated
    public static WebElement find(By by) {
        return MobileSearchUtils.waitForVisible(by);
    }

    @Deprecated
    public static WebElement find(By by, int timeout) {
        return MobileSearchUtils.waitForVisible(by);
    }

    @Deprecated
    public static WebElement filterVisible(String... string) {
        return MobileSearchUtils.findVisible(visibleText(string));

    }

}
