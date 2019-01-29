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

import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class ByTextInBetween extends ByTextIos {
    private String[] filter;
    private String[] before;
    private String[] after;

    private ByTextInBetween(String[] text, String[] before, String[] after) {
        this.filter = text;
        this.before = before;
        this.after = after;
    }

    public static BetweenBuilder visiblePart(String... text) {
        return new BetweenBuilder(text);
    }

    @Override
    protected List<String> lines() {
        return PageUtils.getLinesContaining(PageUtils.getLinesBetween(after, before), filter);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("By.visibleText : ").append(Arrays.toString(filter));
        if (!ArrayUtils.isEmpty(before)) builder.append(" before ").append(Arrays.toString(before));
        if (!ArrayUtils.isEmpty(after)) builder.append(" after ").append(Arrays.toString(after));
        return builder.toString();
    }

    public static class BetweenBuilder extends By {
        private String[] filter;
        private String[] before;
        private String[] after;

        BetweenBuilder(String... text) {
            this.filter = text;
        }

        public BetweenBuilder before(String... text) {
            this.before = text;
            return this;
        }

        public BetweenBuilder after(String... text) {
            this.after = text;
            return this;
        }

        public BetweenBuilder after(ByVisibleText by) {
            this.after = by.getFilter();
            return this;
        }

        public BetweenBuilder before(ByVisibleText by) {
            this.before = by.getFilter();
            return this;
        }

        public ByTextInBetween build() {
            return new ByTextInBetween(this.filter, before, after);
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return build().findElements(context);
        }

        @Override
        public String toString() {
            return build().toString();
        }
    }


}
