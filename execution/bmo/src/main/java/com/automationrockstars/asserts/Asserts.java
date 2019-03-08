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
package com.automationrockstars.asserts;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class Asserts {
    private static final List<AssertionError> errors = Lists.newArrayList();
    private static final boolean soft = ConfigLoader.config().getBoolean("assert.soft", false);
    private static final Logger LOG = LoggerFactory.getLogger(Asserts.class);
    private static Thread hook = null;
    private static boolean doScreenshot = ConfigLoader.config().getBoolean("assert.screenshot", true);

    private synchronized static void addError(AssertionError e) {
        LOG.error("Error in test ", e);
        errors.add(e);
        if (hook == null) {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    checkForErrors();
                }
            }));
        }
    }

    public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
        assertThat("", actual, matcher);
    }

    public static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        if (soft) {
            softAssertThat(reason, actual, matcher);
        } else {
            originalAssertThat(reason, false, actual, matcher);
        }
    }

    public static void assertThat(String reason, boolean assertion) {
        if (soft) {
            softAssertThat(reason, assertion);
        } else {
            originalAssertThat(reason, assertion, null, null);
        }
    }

    public static <T> boolean softAssertThat(String reason, T actual, Matcher<? super T> matcher) {
        try {
            originalAssertThat(reason, false, actual, matcher);
            return true;
        } catch (AssertionError e) {
            addError(e);
            return false;
        }
    }

    public static <T> boolean softAssertThat(T actual, Matcher<? super T> matcher) {
        return softAssertThat("", actual, matcher);
    }

    public static boolean softAssertThat(String reason, boolean assertion) {
        try {
            originalAssertThat(reason, assertion, null, null);
            return true;
        } catch (AssertionError e) {
            addError(e);
            return false;
        }
    }

    public synchronized static void checkForErrors() {
        if (!errors.isEmpty()) {
            throw new AssertionError("Errors during test execution: " + Joiner.on("\n").join(errors));
        }
    }

    public static boolean canMakeScreenshot() {
        if (!doScreenshot) return false;
        boolean webdriverScreenshot = false;
        boolean mobileScreeshot = false;
        try {
            mobileScreeshot = (boolean) Class.forName("com.automationrockstars.gir.mobile.MobileFactory").getDeclaredMethod("canScreenshot").invoke(null);
        } catch (Throwable noMobile) {
        }
        try {
            webdriverScreenshot = (boolean) Class.forName("com.automationrockstars.design.gir.webdriver.DriverFactory").getDeclaredMethod("canScreenshot").invoke(null);
        } catch (Throwable noBrowser) {
        }
        return webdriverScreenshot || mobileScreeshot;
    }

    public static byte[] makeScreenshotIfPossible() {
        byte[] screen = null;
        try {
            boolean mobileScreeshot = (boolean) Class.forName("com.automationrockstars.gir.mobile.MobileFactory").getDeclaredMethod("canScreenshot").invoke(null);

            if (mobileScreeshot) {
                screen = (byte[]) Class.forName("com.automationrockstars.gir.mobile.MobileFactory").getDeclaredMethod("getScreenshot").invoke(null);
                return screen;
            }

        } catch (Throwable noMobile) {

        }
        try {
            boolean webdriverScreeshot = (boolean) Class.forName("com.automationrockstars.design.gir.webdriver.DriverFactory").getDeclaredMethod("canScreenshot").invoke(null);
            if (webdriverScreeshot) {
                screen = (byte[]) Class.forName("com.automationrockstars.design.gir.webdriver.DriverFactory").getDeclaredMethod("getScreenshot").invoke(null);
                return screen;
            }
        } catch (Throwable noBrowser) {

        }
        return screen;
    }


    private static final <T> void originalAssertThat(String reason, boolean assertion, T actual, Matcher<? super T> matcher) {
        String prefix = Paths.get("").toAbsolutePath().toString();
        if (new File("target").exists()) {
            prefix += "/target";
        }
        String screenPath = Paths.get(prefix, UUID.randomUUID().toString()).toString() + ".png";
        if (canMakeScreenshot()) {
            reason = String.format("%s (screenshot taken to %s)", reason, screenPath);
        }
        try {
            if (actual == null && (matcher == null || !matcher.matches(actual))) {
                MatcherAssert.assertThat(reason, assertion);
            } else {
                MatcherAssert.assertThat(reason, actual, matcher);
            }
        } catch (Throwable e) {
            if (doScreenshot) {
                byte[] screen = makeScreenshotIfPossible();
                if (screen != null) {

                    try {
                        Files.write(screen, new File(screenPath));
                    } catch (IOException fileIssue) {
                        LOG.error("Screenshot taken, but can't write due to {}", fileIssue.getMessage());
                    }
                }
            }
            Throwables.propagate(e);
        }
    }
}
