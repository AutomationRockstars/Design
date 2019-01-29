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
import com.automationrockstars.gunter.events.TestCaseStart;

public class TestCaseStartImpl extends AbstractTestEvent implements TestCaseStart {

    private static final String TC_NAME = "TestCaseName";

    public TestCaseStartImpl() {
        super();
    }

    public TestCaseStartImpl(String id) {
        super(id);
    }

    @Override
    public EventType getType() {
        return EventType.TEST_CASE_START;
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.TestCaseStart#getName()
     */
    public String getName() {
        return getAttribute(TC_NAME);
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.TestCaseStart#setName(java.lang.String)
     */
    public void setName(String name) {
        attributes().put(TC_NAME, name);
    }


}
