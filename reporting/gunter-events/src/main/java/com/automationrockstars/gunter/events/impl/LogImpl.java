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
import com.automationrockstars.gunter.events.Log;

public class LogImpl extends AbstractTestEvent implements Log {

    private static final String LOG_LEVEL = "logLevel";
    private static final String LOG_MSG = "logMessage";
    private static final String LOG_ORIGIN = "logOrigin";

    public LogImpl() {
        super();
    }
    public LogImpl(String parentId) {
        super(parentId);
    }

    @Override
    public EventType getType() {
        return EventType.TEST_LOG;
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.Log#log(java.lang.String, java.lang.String, java.lang.String)
     */
    public void log(String level, String origin, String message) {
        attributes().put(LOG_LEVEL, level);
        attributes().put(LOG_MSG, message);
        attributes().put(LOG_ORIGIN, origin);
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.Log#getLevel()
     */
    public String getLevel() {
        return getAttribute(LOG_LEVEL);
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.Log#getMessage()
     */
    public String getMessage() {
        return getAttribute(LOG_MSG);
    }

    /* (non-Javadoc)
     * @see com.automationrockstars.gunter.events.impl.Log#getOrigin()
     */
    public String getOrigin() {
        return getAttribute(LOG_ORIGIN);
    }


}
