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
 * Annotate UiPart class or method with this annotation specifying the class implementing {@link FindByAugmenter}
 * e.g.
 * <p>
 * {@literal @}FindBy(id="DYNAMIC_PLACEHOLDER_span")
 * {@literal @}WithFindAugmenter(DynamicPlaceholderResolver.class)
 * public interface DynamicDiv extends UiPart {
 * ...
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WithFindByAugmenter {

    Class<? extends FindByAugmenter> value();
}
