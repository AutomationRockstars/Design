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
import com.automationrockstars.gunter.events.TestSuiteStart;

public class TestSuiteStartImpl extends AbstractTestEvent implements TestSuiteStart {

    private final static String TS_NAME = "TestSuiteName";

    public TestSuiteStartImpl() {
        super();
    }

    public TestSuiteStartImpl(String parentId) {
        super(parentId);
    }

    @Override
    public EventType getType() {
        return EventType.TEST_SUITE_START;
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.TestSuiteStart#getTestSuiteName()
     */
    public String getTestSuiteName() {
        return getAttribute(TS_NAME);
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.TestSuiteStart#setTestSuiteName(java.lang.String)
     */
    public void setTestSuiteName(String name) {
        attributes().put(TS_NAME, name);
    }
}
