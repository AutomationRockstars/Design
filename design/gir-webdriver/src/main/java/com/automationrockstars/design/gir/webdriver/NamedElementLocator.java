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
package com.automationrockstars.design.gir.webdriver;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.pagefactory.AjaxElementLocator;
import ru.yandex.qatools.htmlelements.pagefactory.AnnotationsHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NamedElementLocator extends AjaxElementLocator {

    private static final Logger LOG = LoggerFactory.getLogger(NamedElementLocator.class);
    private String name;

    public NamedElementLocator(SearchContext context, int timeOutInSeconds, AnnotationsHandler annotationsHandler) {
        super(context, timeOutInSeconds, annotationsHandler);
    }

    public NamedElementLocator withField(Field field) {
        LOG.info("Field {} has annotation {}", field, field.getAnnotations());
        if (field.getAnnotation(Name.class) != null) {
            name = field.getAnnotation(Name.class).value();
        }
        return this;
    }

    public NamedElementLocator withClass(Class<?> klass) {
        LOG.info("Class {} has annotation {}", klass, klass.getAnnotations());
        if (klass.getAnnotation(Name.class) != null) {
            name = klass.getAnnotation(Name.class).value();
        }
        return this;
    }

    public WebElement findElement() {
        WebElement result = super.findElement();

        try {
            result.getClass().getMethod("setName", String.class).invoke(result, name);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException ignore) {

        }

        return result;
    }


}
