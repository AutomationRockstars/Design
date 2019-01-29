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
package com.automationrockstars.design.gir.webdriver.plugin;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.internal.Coordinates;

import java.util.List;
import java.util.ServiceLoader;

public class UiObjectInfoPluginService {

    private static final List<UiObjectInfoPlugin> plugins = Lists.newArrayList();
    private static final UiObjectInfoPlugin instance = new CompositeInfoPlugin();

    static {
        registerSpiPlugins();
    }

    public static final UiObjectInfoPlugin infoPlugins() {
        return instance;
    }

    public static final List<UiObjectInfoPlugin> getPlugins() {
        return ImmutableList.copyOf(plugins);
    }

    public static final void registerPlugin(UiObjectInfoPlugin plugin) {
        plugins.add(plugin);
    }

    private static final void registerSpiPlugins() {
        plugins.addAll(Lists.newArrayList(ServiceLoader.load(UiObjectInfoPlugin.class).iterator()));
    }

    public static class CompositeInfoPlugin implements UiObjectInfoPlugin {

        public void beforeGetTagName(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetTagName(element);
            }

        }

        public void afterGetTagName(UiObject element, String value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetTagName(element, value);
            }

        }

        public void beforeGetAttribute(UiObject element, String name) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetAttribute(element, name);
            }

        }

        public void afterGetAttribute(UiObject element, String name, String value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetAttribute(element, name, value);
            }

        }

        public void beforeIsSelected(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeIsSelected(element);
            }

        }

        public void afterIsSelected(UiObject element, boolean value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterIsSelected(element, value);
            }

        }

        public void beforeIsEnabled(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeIsEnabled(element);
            }

        }

        public void afterIsEnabled(UiObject element, boolean value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterIsEnabled(element, value);
            }

        }

        public void beforeGetText(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetText(element);
            }

        }

        public void afterGetText(UiObject element, String value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetText(element, value);
            }

        }

        public void beforeIsDisplayed(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeIsDisplayed(element);
            }

        }

        public void afterIsDisplayed(UiObject element, boolean value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterIsDisplayed(element, value);
            }

        }

        public void beforeGetLocation(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetLocation(element);
            }

        }

        public void afterGetLocation(UiObject element, Point value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetLocation(element, value);
            }

        }

        public void beforeGetSize(UiObject element) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetSize(element);
            }

        }

        public void afterGetSize(UiObject element, Dimension value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetSize(element, value);
            }

        }

        public void beforeGetCssValue(UiObject element, String propertyName) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetCssValue(element, propertyName);
            }

        }

        public void afterGetCssValue(UiObject element, String propertyName, String value) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetCssValue(element, propertyName, value);
            }

        }

        @Override
        public <X> void beforeGetScreenshotAs(UiObject element, OutputType<X> target) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetScreenshotAs(element, target);
            }

        }

        @Override
        public <X> void afterGetScreenshotAs(UiObject element, OutputType<X> target, X screenshot) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetScreenshotAs(element, target, screenshot);
            }
        }

        @Override
        public void beforeGetCoordinates(UiObject uiObject) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetCoordinates(uiObject);
            }

        }

        @Override
        public void afterGetCoordinates(UiObject uiObject, Coordinates result) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetCoordinates(uiObject, result);
            }

        }

        @Override
        public void beforeGetRect(UiObject uiObject) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.beforeGetRect(uiObject);
            }

        }

        @Override
        public void afterGetRect(UiObject uiObject, Rectangle rect) {
            for (UiObjectInfoPlugin plugin : plugins) {
                plugin.afterGetRect(uiObject, rect);
            }

        }

    }

}
