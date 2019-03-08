/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gunter.jenkins;

import com.automationrockstars.gunter.jenkins.rabbitmq.GlobalGunterConfiguration;
import hudson.Plugin;

import java.util.logging.Logger;


public class PluginImpl extends Plugin {
    private final static Logger LOG = Logger.getLogger(PluginImpl.class.getName());

    public void start() throws Exception {
        LOG.info("Gunter plugin to send job event to RabbitMQ");

        if (GlobalGunterConfiguration.get() != null && GlobalGunterConfiguration.get().getHost() != null) {
            LOG.info("Configuration set to :" + GlobalGunterConfiguration.get());
        } else {
            LOG.warning("RabbitMQ connection is not configured");
        }

    }

}
