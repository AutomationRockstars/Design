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
import com.automationrockstars.gunter.events.environment.EnvironmentBroken;

public class EnvironmentBrokenImpl extends AbstractEnvironmentImpl implements EnvironmentBroken {

    private static final String CAUSE = "CAUSE";

    public EnvironmentBrokenImpl(String parentId, String name, String cause) {
        super(parentId, name);
        setCause(cause);
    }

    @Override
    public String getCause() {
        return getAttribute(CAUSE);
    }

    public void setCause(String cause) {
        attributes().put(CAUSE, cause);
    }

    @Override
    public EventType getType() {
        return EventType.ENVIRONMENT_BROKEN;
    }

}
