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

package com.automationrockstars.bmo.event.processor;

import com.automationrockstars.bmo.event.processor.internal.RuleReporter;
import com.google.common.base.Predicate;
import org.junit.Test;

import java.lang.reflect.Method;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class SenderReceiverTest {

    @Test
    public void detectRules() {
        assertThat(RuleReporter.validRules().size(), is(greaterThan(0)));
        assertThat("Good rules only", RuleReporter.validRules().allMatch(new Predicate<Method>() {

            @Override
            public boolean apply(Method input) {
                return input.getName().contains("good");
            }
        }));
    }
}
