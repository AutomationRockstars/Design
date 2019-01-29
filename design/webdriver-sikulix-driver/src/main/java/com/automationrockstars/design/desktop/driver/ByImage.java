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

package com.automationrockstars.design.desktop.driver;

import com.automationrockstars.design.desktop.driver.internal.ImageCache;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

public class ByImage extends By {

    private String imagePath;

    public ByImage(String imagePath) {
        if (isImageId(imagePath)) {
            imagePath = translateFromId(imagePath);
        }
        this.imagePath = imagePath;
    }

    public static boolean isImageId(String id) {
        return id.startsWith("image:");
    }

    public static boolean isImageId(By by) {
        return by instanceof ById && isImageId(by.toString().replace("By.id: ", ""));
    }

    public static String translateFromId(String id) {
        Preconditions.checkArgument(isImageId(id), "id %s is not image id", id);
        return id.replace("image:", "");
    }

    public static String translateToId(String imagePath) {
        return String.format("image:%s", imagePath);
    }

    public static ByImage translateFrom(By byId) {
        Preconditions.checkArgument(isImageId(byId), "Only ById specyfying image can be translated");
        return new ByImage((byId.toString().replace("By.id: ", "")));
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
        Preconditions.checkArgument(ImageSearchContext.class.isAssignableFrom(context.getClass()) || context.toString().toLowerCase().contains("sikuli"), "ByImage works only with SikuliDriver");
        if (context instanceof RemoteWebDriver || context instanceof RemoteWebElement)
            return searchRemote(imagePath, context);
        List<WebElement> result = Lists.newArrayList(Iterators.transform(((ImageSearchContext) context).findElements(imagePath), new Function<ImageUiObject, WebElement>() {
            @Override
            public WebElement apply(ImageUiObject input) {
                return input;
            }
        }));
        return result;

    }

    public String path() {
        return imagePath;
    }

    private List<WebElement> searchRemote(String imagePath, SearchContext ctx) {
        if (WebDriver.class.isAssignableFrom(ctx.getClass())) {
            ImageCache.syncRemote((WebDriver) ctx);
        }
        return ((FindsById) ctx).findElementsById(translateToId(imagePath));
    }

    public String toString() {
        return String.format("element matching image %s", imagePath);
    }

}
