/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gunter.events.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.TestStepFinish;

public class TestStepFinishImpl extends TestStepStartImpl implements TestStepFinish {

    private static final String TEST_STEP_STATUS = "TestStepStatus";
    private static final String CAUSE = "CAUSE";

    public TestStepFinishImpl() {
        super();
    }

    public TestStepFinishImpl(String id) {
        super(id);
    }

    @Override
    public EventType getType() {
        return EventType.TEST_STEP_FINISH;
    }

    @Override
    public String getStatus() {
        return attributes().get(TEST_STEP_STATUS).toString();
    }

    @Override
    public void setStatus(String status) {
        attributes().put(TEST_STEP_STATUS, status);
    }

    @Override
    public String getCause() {
        return getAttribute(CAUSE);
    }

    public void setCause(String cause) {
        attributes().put(CAUSE, cause);
    }

}
