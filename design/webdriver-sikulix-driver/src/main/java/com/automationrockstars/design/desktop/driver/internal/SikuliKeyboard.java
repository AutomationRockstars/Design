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

import com.google.common.base.Joiner;
import org.openqa.selenium.interactions.Keyboard;
import org.sikuli.script.Region;

public class SikuliKeyboard implements Keyboard {

    private final Region region;


    public SikuliKeyboard(Region region) {
        this.region = region;
    }


    @Override
    public void sendKeys(CharSequence... keysToSend) {
        region.type(Joiner.on("").join(keysToSend));

    }

    @Override
    public void pressKey(CharSequence keyToPress) {
        region.keyDown(keyToPress.toString());

    }

    @Override
    public void releaseKey(CharSequence keyToRelease) {
        region.keyUp(keyToRelease.toString());

    }

}
