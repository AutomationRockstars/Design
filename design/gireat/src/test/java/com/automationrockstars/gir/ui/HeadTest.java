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

package com.automationrockstars.gir.ui;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Optional;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

public class HeadTest {

    @BeforeClass
    public static void browser() {
        DriverFactory.getDriver().get("http://www.google.ie");

    }

    @AfterClass
    public static void stopBrowser() {
        DriverFactory.closeDriver();
    }

    @Test
    public void should_returnMeta() {
        assertThat(UiParts.head().meta().size(), greaterThan(0));
    }

    @Test
    public void should_returnTitle() {
        assertThat(DriverFactory.getDriver().getTitle(), not(isEmptyOrNullString()));
    }

    @Test
    public void should_returnScript() {
        assertThat(UiParts.head().script().first(), is(not(Optional.<WebElement>absent())));
    }

    @Test
    public void should_returnStyle() {
        assertThat(UiParts.head().style().first(), is(not(Optional.<WebElement>absent())));
    }
}

