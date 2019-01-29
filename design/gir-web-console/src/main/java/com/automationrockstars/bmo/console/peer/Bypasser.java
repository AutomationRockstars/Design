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
package com.automationrockstars.bmo.console.peer;

import com.automationrockstars.bmo.console.traffic.ConsoleEventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class Bypasser {

    @Subscribe
    public void lame(HttpResponse r) {
        ConsoleEventBus.execution.post(r);
    }

    @Subscribe
    public void lame(HttpRequest r) {
        ConsoleEventBus.execution.post(r);
    }
}
