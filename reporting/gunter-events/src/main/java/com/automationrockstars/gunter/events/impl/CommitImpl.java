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
import com.automationrockstars.gunter.events.Commit;

import java.util.Map;

public class CommitImpl extends AbstractTestEvent implements Commit {


    private static final String CONTENT = "CONTENT";

    public CommitImpl() {
        super();
    }

    public CommitImpl(String parent) {
        super(parent);
    }

    @Override
    public Map<String, String> getContent() {
        return (Map<String, String>) attributes().get(CONTENT);
    }

    @Override
    public void setContent(Map<String, String> content) {
        attributes().put(CONTENT, content);

    }

    @Override
    public EventType getType() {
        return EventType.COMMIT;

    }


}
