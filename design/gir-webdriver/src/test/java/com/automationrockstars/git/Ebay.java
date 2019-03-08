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

package com.automationrockstars.git;

import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class Ebay {


    static {
        DriverFactory.getDriver().get("http://www.ebay.ie/");
    }

    public static EbayPageHeader page() {
        return EbayPageHeader.waitFor();
    }

    public static String searchFor(String whatFor) {
        return page().getText();
    }
}
