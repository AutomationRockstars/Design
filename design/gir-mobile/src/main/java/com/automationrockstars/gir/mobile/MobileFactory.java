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
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.automationrockstars.base.ConfigLoader.config;

public class MobileFactory {

    public static final String ANDROID = "ANDROID";
    public static final String IOS = "IOS";
    public static final String UNKNOWN = "UNKNOWN";
    private static final Logger LOG = LoggerFactory.getLogger(MobileFactory.class);
    static AppiumDriver driver;
    static int DEFAULT_DELAY = ConfigLoader.config().getInt("mobile.delay", 60);
    static int DEFAULT_POLL = ConfigLoader.config().getInt("mobile.poll", 8000);

    public static DriverBuilder builder() {
        return new DriverBuilder();
    }

    public static AppiumDriver getDriver() {
        if (driver == null) {
            LOG.info("Creating new driver from properties");
            DriverBuilder builder = builder();
            if (!config().getBoolean("app.reset", false)) {
                builder.dontReset();
            }
            if ("ios".equalsIgnoreCase(config().getString("mobile.platform"))) {
                prepareIos(builder);
            } else {
                prepareAndroid(builder);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    MobileFactory.resetDriver();
                }
            }));
        }
        return driver;
    }

    private static void prepareAndroid(DriverBuilder builder) {
        builder
                .android()
                .forActivity(config().getString("android.app.activity"))
                .forPackage(config().getString("android.app.package"))
                .forApp(androidApp())
                .build();
    }

    public static String androidApp() {
        return config().getString("app.directory") + config().getString("android.app.file");
    }

    public static String iPhoneApp() {
        return config().getString("app.directory") + config().getString("ios.app.file");
    }

    private static void prepareIos(DriverBuilder builder) {
        builder.iOS().
                forApp(iPhoneApp())
                .build();
    }

    public static void resetDriver() {
        if (driver != null) {
            try {
                driver.closeApp();
                driver.quit();
            } catch (Exception ignore) {
            }
        }
        driver = null;
        LOG.info("Driver unset");
    }

    public static boolean isAndroid() {
        if (driver != null) {
            return getDriver() instanceof AndroidDriver;
        } else {
            return "android".equalsIgnoreCase(config().getString("mobile.platform"));
        }
    }

    public static boolean isIOS() {
        if (driver != null) {
            return getDriver() instanceof IOSDriver;
        } else {
            return "ios".equalsIgnoreCase(config().getString("mobile.platform"));
        }
    }

    public static String currentPlatform() {
        if (isAndroid()) {
            return ANDROID;
        } else if (isIOS()) {
            return IOS;
        } else return UNKNOWN;
    }

    public static boolean canScreenshot() {
        return driver != null;
    }

    public static byte[] getScreenshot() {
        return driver.getScreenshotAs(OutputType.BYTES);
    }

    public static WebDriverWait delay() {
        return delay(DEFAULT_DELAY);
    }

    public static WebDriverWait delay(int seconds) {
        return new WebDriverWait(MobileFactory.getDriver(), seconds, DEFAULT_POLL);

    }

}
