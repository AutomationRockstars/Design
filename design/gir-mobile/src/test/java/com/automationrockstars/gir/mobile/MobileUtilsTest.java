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

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class MobileUtilsTest {

    @Ignore
    @Test
    public void basicDriver() throws MalformedURLException {

        DesiredCapabilities cap = DesiredCapabilities.android();
        cap.setCapability("deviceName", "android phone");
        AppiumDriver<WebElement> d = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), cap);
        d.quit();

    }

}
