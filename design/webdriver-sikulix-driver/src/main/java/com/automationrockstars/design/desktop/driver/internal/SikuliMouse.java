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

package com.automationrockstars.design.desktop.driver.internal;

import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.Coordinates;
import org.sikuli.script.Button;
import org.sikuli.script.Region;

public class SikuliMouse implements Mouse {


    private Region region;


    public SikuliMouse(Region region) {
        this.region = region;
    }

    @Override
    public void click(Coordinates where) {
        region.click();

    }

    @Override
    public void doubleClick(Coordinates where) {
        region.doubleClick();

    }

    @Override
    public void mouseDown(Coordinates where) {
        region.mouseDown(Button.LEFT);

    }

    @Override
    public void mouseUp(Coordinates where) {
        region.mouseUp();

    }

    @Override
    public void mouseMove(Coordinates where) {
        region.mouseDown(Button.LEFT);

    }

    @Override
    public void mouseMove(Coordinates where, long xOffset, long yOffset) {
        region.mouseMove((int) xOffset, (int) yOffset);

    }

    @Override
    public void contextClick(Coordinates where) {
        region.mouseDown(Button.RIGHT);
        region.mouseUp();

    }

}
