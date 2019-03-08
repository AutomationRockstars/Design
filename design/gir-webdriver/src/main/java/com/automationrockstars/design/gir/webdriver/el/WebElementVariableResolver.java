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

package com.automationrockstars.design.gir.webdriver.el;

import com.google.common.collect.Maps;
import org.mvel2.integration.VariableResolver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class WebElementVariableResolver implements VariableResolver {

    /**
     *
     */
    private static final long serialVersionUID = -1725557044733700005L;
    private final WebElement element;
    private final String name;
    private final Map<String, Object> cached = Maps.newHashMap();

    public WebElementVariableResolver(WebElement element, String name) {
        this.element = element;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("rawtypes")
    public Class getType() {
        return WebElement.class;
    }

    @SuppressWarnings("rawtypes")
    public void setStaticType(Class type) {
        System.out.println("set class " + type);
    }

    public int getFlags() {
        return 0;
    }

    public Object getValue() {
        if (cached.get(name) == null) {
            cached.put(name, getOrigValue());
        }
        return cached.get(name);
    }

    public void setValue(Object value) {
        System.out.println("set " + value);
    }

    public Object getOrigValue() {
        if (name == null) {
            return null;
        }
        try {
            switch (name.toLowerCase()) {
                case "text":
                    return element.getText();
                case "tag":
                    return element.getTagName();
                case "size":
                    return element.getSize();
                case "rect":
                    return element.getRect();
                case "displayed":
                    return element.isDisplayed();
                case "enabled":
                    return element.isEnabled();
                case "selected":
                    return element.isSelected();
                case "element":
                    return element;
                case "find":
                    return new Finder();
                default:
                    Object result = null;
                    try {
                        result = element.getAttribute(name);
                        if (result == null) {
                            result = element.getCssValue(name);
                        }
                    } catch (Throwable e) {
                        try {
                            return element.getCssValue(name);
                        } catch (Throwable ignore) {

                        }
                    }
                    return result;
            }
        } catch (Throwable someIssue) {
            return null;
        }

    }

}
