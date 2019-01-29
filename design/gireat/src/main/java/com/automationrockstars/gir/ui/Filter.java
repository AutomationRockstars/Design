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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Uses expression language to filter elements from list of elements returned by original lookup using By
 * Available variables:
 * element - WebElement to be filtered
 * text - text inside WebElement
 * tag, size, rect, displayed, enabled, selected
 * every other value is evaluated using 1) getAttribute method 2) getCssValue
 * <p>
 * Result of EL statement needs to be boolean
 * <p>
 * E.g. text.contains('automationrockstars') will return sub set of all elements containing text "automationrockstars"
 * <p>
 * {@literal @}FindBy(tagName="div")
 * {@literal @}Filter("text.contains('GitHub')")
 * WebElement githubDiv();
 * <p>
 * Calling githubDiv() will return a WebElement with tag div and containing GitHub text inside
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Filter {

    String value();
}
