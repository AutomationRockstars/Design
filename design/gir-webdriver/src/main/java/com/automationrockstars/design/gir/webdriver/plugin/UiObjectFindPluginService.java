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
package com.automationrockstars.design.gir.webdriver.plugin;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.ServiceLoader;

public class UiObjectFindPluginService {

    private static final List<UiObjectFindPlugin> plugins = Lists.newArrayList();
    private static final UiObjectFindPlugin instance = new CompositeFindPlugin();

    static {
        registerSpiPlugins();
    }

    public static final UiObjectFindPlugin findPlugins() {
        return instance;
    }

    public static List<UiObjectFindPlugin> getPlugins() {
        return ImmutableList.copyOf(plugins);
    }

    public static void registerPlugin(UiObjectFindPlugin plugin) {
        plugins.add(plugin);
    }

    private static final void registerSpiPlugins() {
        plugins.addAll(Lists.newArrayList(ServiceLoader.load(UiObjectFindPlugin.class).iterator()));
    }

    public static class CompositeFindPlugin implements UiObjectFindPlugin {

        public void beforeFindElements(UiObject element, By by) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.beforeFindElements(element, by);
            }

        }

        public void afterFindElements(UiObject element, By by, List<WebElement> result) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.afterFindElements(element, by, result);
            }

        }

        public void beforeFindElement(UiObject element, By by) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.beforeFindElement(element, by);
            }

        }

        public void afterFindElement(UiObject element, By by, WebElement result) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.afterFindElement(element, by, result);
            }

        }

        @Override
        public void beforeWaitForVisible(UiObject element) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.beforeWaitForVisible(element);
            }

        }

        @Override
        public void afterWaitForVisible(UiObject element) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.afterWaitForVisible(element);
            }

        }

        @Override
        public void beforeWaitForPresent(UiObject element) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.beforeWaitForPresent(element);
            }

        }

        @Override
        public void afterWaitForPresent(UiObject element) {
            for (UiObjectFindPlugin plugin : plugins) {
                plugin.afterWaitForPresent(element);
            }

        }

    }
}
