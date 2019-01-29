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

package com.automationrockstars.gunter.events.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Sample;

import java.util.Map;

public class SampleImpl extends AbstractTestEvent implements Sample {


    private static final String HOST = "HOST";
    private static final String TYPE = "SAMPLE_TYPE";
    private String CONTENT = "SAMPLE";

    @Override
    public Map<String, Number> getSample() {
        return getAttribute(CONTENT);
    }

    @Override
    public void setSample(Map<String, Number> content) {
        setAttribute(CONTENT, content);

    }

    @Override
    public EventType getType() {
        return EventType.SAMPLE;
    }

    @Override
    public String getHost() {
        return getAttribute(HOST);
    }

    @Override
    public void setHost(String host) {
        setAttribute(HOST, host);

    }

    @Override
    public String getSampleType() {
        return getAttribute(TYPE);
    }

    @Override
    public void setSampleType(String type) {
        setAttribute(TYPE, type);

    }

}
