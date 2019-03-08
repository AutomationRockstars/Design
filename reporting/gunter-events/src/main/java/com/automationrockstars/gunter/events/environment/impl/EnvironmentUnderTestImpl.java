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

package com.automationrockstars.gunter.events.environment.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.environment.EnvironmentUnderTest;

public class EnvironmentUnderTestImpl extends AbstractEnvironmentImpl implements EnvironmentUnderTest {


    public EnvironmentUnderTestImpl(String parentId, String name) {
        super(parentId, name);
    }

    @Override
    public EventType getType() {
        return EventType.ENVIRONMENT_UNDER_TESTS;
    }

}
