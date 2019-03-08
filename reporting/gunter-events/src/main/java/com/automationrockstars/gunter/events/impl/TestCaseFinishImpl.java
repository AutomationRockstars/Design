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
import com.automationrockstars.gunter.events.TestCaseFinish;
import com.automationrockstars.gunter.events.TestCaseStart;

public class TestCaseFinishImpl extends TestCaseStartImpl implements TestCaseFinish {

    private static final String TC_STATUS = "TestCaseStatus";

    public TestCaseFinishImpl() {
        super();
    }

    public TestCaseFinishImpl(TestCaseStart parent) {
        super(parent.getId());
        super.setName(parent.getName());
    }

    @Override
    public EventType getType() {
        return EventType.TEST_CASE_FINISH;
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.TestCaseFinish#getStatus()
     */
    public String getStatus() {
        return getAttribute(TC_STATUS);
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.TestCaseFinish#setStatus(java.lang.String)
     */
    public void setStatus(String status) {
        attributes().put(TC_STATUS, status);
    }

}
