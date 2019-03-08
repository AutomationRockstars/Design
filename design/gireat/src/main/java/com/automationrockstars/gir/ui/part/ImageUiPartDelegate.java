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

package com.automationrockstars.gir.ui.part;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.context.SearchContextService;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.List;

public class ImageUiPartDelegate extends AbstractUiPartDelegate {

    public ImageUiPartDelegate(Class<? extends UiPart> view) {
        super(view);

    }

    public ImageUiPartDelegate(Class<? extends UiPart> view, UiObject toWrap) {
        super(view, toWrap);

    }

    @Override
    public FluentIterable<UiObject> children() {
        List<UiObject> empty = Lists.newArrayList();
        return FluentIterable.from(empty);
    }

    @Override
    public boolean has(By element) {
        try {
            return getWrappedElement().findElement(element) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasText(String text) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPresent() {
        try {
            this.wrapped = null;
            return this.getWrappedElement() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isVisible() {
        return getWrappedElement() != null;
    }

    @Override
    public void waitForHidden() {
        // TODO Auto-generated method stub

    }

    @Override
    public UiObject childWithText(String text) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Link childLinkWithText(String text) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public WebElement getWrappedElement() {
        wrapped = (UiObject) SearchContextService.provideForImage().findElement(getLocator());
        return wrapped;
    }


}
