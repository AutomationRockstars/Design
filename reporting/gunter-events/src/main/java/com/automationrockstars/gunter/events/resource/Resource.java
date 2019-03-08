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

package com.automationrockstars.gunter.events.resource;

import com.automationrockstars.gunter.events.Event;

import java.util.Map;

public interface Resource extends Event {

    String getHostName();

    String getMetricType();

    Map<String, Object> getSample();

}
