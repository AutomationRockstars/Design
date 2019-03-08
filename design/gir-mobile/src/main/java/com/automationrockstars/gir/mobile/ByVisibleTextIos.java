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

import com.google.common.collect.Lists;
import io.appium.java_client.MobileBy.ByIosUIAutomation;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ByVisibleTextIos extends ByVisibleText {

    public static final String VISIBLE = "visible=\"true";

    public ByVisibleTextIos(String... filter) {
        super(filter);
    }

    static WebElement forLine(SearchContext context, String line) {
        String search = PageUtils.getValueFromLine("path", line);
        By path = new ByIosUIAutomation("au.getElementsByIndexPaths([\"" + search + "\"]);");
        WebElement part = null;
        try {
            part = path.findElement(context);
        } catch (Throwable t) {
            try {
                String s = search.replaceAll("/.$", "");

                WebElement app = new ByIosUIAutomation("au.getElementsByIndexPaths([\"" + s + "\"]);").findElement(MobileFactory.getDriver());
                part = new NotDisplayedYetVisibleUiObject(line, app);
            } catch (Exception ignore) {
            }
            ;
        }
        return part;
    }

    public List<WebElement> findElements(final SearchContext context, final List<String> lines) {
        List<WebElement> result = Lists.newArrayList();
        for (String line : lines) {
            WebElement part;
            if ((part = forLine(context, line)) != null) {
                result.add(part);
            }
        }
        return result;
    }

    public void addVisible() {
        if (!ArrayUtils.contains(filter, VISIBLE)) {
            filter = ArrayUtils.add(filter, VISIBLE);
        }
    }
}
