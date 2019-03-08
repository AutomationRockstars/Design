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

package com.automationrockstars.gir.ui.context;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import org.openqa.selenium.SearchContext;

import java.lang.annotation.Annotation;

public class WebdriverSearchContextProvider implements SearchContextProvider {

    @Override
    public boolean canProvide(Class<? extends Annotation> context) {
        return Web.class.equals(context);
    }

    @Override
    public SearchContext provide() {
        return DriverFactory.getDriver();
    }

}
