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

import com.automationrockstars.design.gir.webdriver.UiFragment;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.FindBy;

@FindBy(id = "headerFragment")
public class EbayPageHeader extends UiFragment {

    public static EbayPageHeader waitFor() {
        return new EbayPageHeader();
    }

    @Override
    public void setLocator(By by) {
        // TODO Auto-generated method stub

    }

    @Override
    public Rectangle getRect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        // TODO Auto-generated method stub
        return null;
    }

}
