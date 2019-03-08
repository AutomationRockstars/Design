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

import com.google.common.collect.Lists;

import java.util.List;

public class GoogleErrors {

    @Globals
    public static List<org.openqa.selenium.By> errorElements() {
        return Lists.newArrayList(
                new FilteredBy(org.openqa.selenium.By.tagName("div"), "text.contains('Error')"),
                org.openqa.selenium.By.partialLinkText("Error")
        );
    }
}
