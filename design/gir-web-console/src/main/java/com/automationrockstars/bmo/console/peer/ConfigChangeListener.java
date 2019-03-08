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
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

public class ConfigChangeListener implements ConfigurationListener {

    @Override
    public void configurationChanged(ConfigurationEvent event) {
        if (!event.isBeforeUpdate()) {
            ConsoleEventBus.execution.post(event);
        }
    }

}
