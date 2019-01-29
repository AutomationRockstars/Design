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

import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.FindByAugmenter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import org.openqa.selenium.By;
import org.openqa.selenium.By.*;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.How;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.openqa.selenium.support.How.*;

/**
 * Utility class for FindByAugmenter operations
 */
public class FindByAugmenters {

    /**
     * Create or retrieve instance of FindByAugmenter
     *
     * @param value
     * @return
     */
    public static FindByAugmenter instance(Class<? extends FindByAugmenter> value) {
        try {
            return value.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            Throwables.propagate(e);
            return null;
        }
    }

    /**
     * Find out what kind of FindBy is passed to method. It works for long ({@literal @}FindBy(how = How.ID, using="someId"))
     * and short ({@literal @}FindBy(id="someId)) FindBy annotations returning same result for both
     *
     * @param toBeAugmented
     * @return
     */
    public static How how(FindBy toBeAugmented) {
        if (toBeAugmented.how() != null && (!toBeAugmented.how().equals(How.UNSET))) {
            return toBeAugmented.how();
        } else if (!isNullOrEmpty(toBeAugmented.id())) {
            return ID;
        } else if (!isNullOrEmpty(toBeAugmented.className())) {
            return CLASS_NAME;
        } else if (!isNullOrEmpty(toBeAugmented.css())) {
            return CSS;
        } else if (!isNullOrEmpty(toBeAugmented.linkText())) {
            return LINK_TEXT;
        } else if (!isNullOrEmpty(toBeAugmented.name())) {
            return NAME;
        } else if (!isNullOrEmpty(toBeAugmented.partialLinkText())) {
            return How.PARTIAL_LINK_TEXT;
        } else if (!isNullOrEmpty(toBeAugmented.xpath())) {
            return XPATH;
        } else if (!isNullOrEmpty(toBeAugmented.tagName())) {
            return TAG_NAME;
        } else return How.UNSET;
    }


    /**
     * Method to retrieve string value of FindBy annotation. It works for long ({@literal @}FindBy(how = How.ID, using="someId"))
     * and short ({@literal @}FindBy(id="someId)) FindBy annotations returning same result for both
     *
     * @param toBeAugmented
     * @return
     */
    public static String using(FindBy toBeAugmented) {
        if (!Strings.isNullOrEmpty(toBeAugmented.using())) {
            return toBeAugmented.using();
        } else if (!isNullOrEmpty(toBeAugmented.id())) {
            return toBeAugmented.id();
        } else if (!isNullOrEmpty(toBeAugmented.className())) {
            return toBeAugmented.className();
        } else if (!isNullOrEmpty(toBeAugmented.css())) {
            return toBeAugmented.css();
        } else if (!isNullOrEmpty(toBeAugmented.linkText())) {
            return toBeAugmented.linkText();
        } else if (!isNullOrEmpty(toBeAugmented.name())) {
            return toBeAugmented.name();
        } else if (!isNullOrEmpty(toBeAugmented.partialLinkText())) {
            return toBeAugmented.partialLinkText();
        } else if (!isNullOrEmpty(toBeAugmented.xpath())) {
            return toBeAugmented.xpath();
        } else if (!isNullOrEmpty(toBeAugmented.tagName())) {
            return toBeAugmented.tagName();
        } else return null;
    }

    /**
     * Returns Class extending {@link org.openqa.selenium.By} for provided FindBy annotation
     *
     * @param toBeAugmented
     * @return
     */
    public static Class<? extends By> classFor(FindBy toBeAugmented) {
        switch (how(toBeAugmented)) {
            case ID:
                return ById.class;
            case CLASS_NAME:
                return ByClassName.class;
            case CSS:
                return ByCssSelector.class;
            case ID_OR_NAME:
                return ByIdOrName.class;
            case LINK_TEXT:
                return ByLinkText.class;
            case NAME:
                return ByName.class;
            case PARTIAL_LINK_TEXT:
                return ByPartialLinkText.class;
            case TAG_NAME:
                return ByTagName.class;
            case XPATH:
                return ByXPath.class;
            default:
                return null;
        }
    }

    /**
     * Method returns constructor of class extending {@link org.openqa.selenium.By} that accepts one String parameter
     * as it would be executed directly from FindBy annotation processor
     *
     * @param toBeAugmented
     * @return
     */
    public static Constructor<? extends By> constructorFor(FindBy toBeAugmented) {
        try {
            final Class<? extends By> byClass = classFor(toBeAugmented);
            Preconditions.checkArgument(byClass != null, "Cannot find class for FindBy %s", toBeAugmented);
            return byClass.getConstructor(String.class);
        } catch (NoSuchMethodException | SecurityException e) {
            Throwables.propagate(e);
            return null;
        }
    }

    /**
     * Method returns instance of class extending {@link org.openqa.selenium.By} created with constructor accepting String.
     * The String value used for constructing By is second parameter of the method
     *
     * @param toBeAugmented
     * @param newValue
     * @return
     */
    public static By instanceForValue(FindBy toBeAugmented, String newValue) {
        try {
            return constructorFor(toBeAugmented).newInstance(newValue);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            Throwables.propagate(e);
            return null;
        }
    }


    static FindBy translate(final org.openqa.selenium.support.FindBy original) {
        return new FindBy() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return FindBy.class;
            }

            @Override
            public String xpath() {
                return original.xpath();
            }

            @Override
            public String using() {
                return original.using();
            }

            @Override
            public String tagName() {
                return original.tagName();
            }

            @Override
            public String partialLinkText() {
                return original.partialLinkText();
            }

            @Override
            public String name() {
                return original.name();
            }

            @Override
            public String linkText() {
                return original.linkText();
            }

            @Override
            public String id() {
                return original.id();
            }

            @Override
            public How how() {
                return original.how();
            }

            @Override
            public String css() {
                return original.css();
            }

            @Override
            public String className() {
                return original.className();
            }
        };
    }

}
