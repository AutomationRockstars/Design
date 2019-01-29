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
package com.automationrockstars.git;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class WebdriverTest {


    @Test
    public void test() throws InterruptedException, IOException {
        System.setProperty("noui", "true");
        assertThat(Ebay.searchFor("aa"), is(not(nullValue())));


    }

    @Test
    public void checkItSearch() {
        System.setProperty("noui", "true");
        DriverFactory.getDriver().get("http://ebay.com");
        DriverFactory.getDriver().findElement(By.tagName("body")).findElement(By.tagName("body"));
    }

}
