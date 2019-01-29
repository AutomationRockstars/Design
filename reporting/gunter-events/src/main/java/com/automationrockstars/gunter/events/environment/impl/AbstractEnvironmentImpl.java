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

import com.automationrockstars.gunter.events.environment.Environment;
import com.automationrockstars.gunter.events.impl.AbstractTestEvent;

public abstract class AbstractEnvironmentImpl extends AbstractTestEvent implements Environment {

    private static final String NAME = "NAME";

    public AbstractEnvironmentImpl(String parentId, String name) {
        super(parentId);
        setName(name);
    }

    @Override
    public String getName() {
        return getAttribute(NAME);

    }

    public void setName(String name) {
        attributes().put(NAME, name);
    }


}
