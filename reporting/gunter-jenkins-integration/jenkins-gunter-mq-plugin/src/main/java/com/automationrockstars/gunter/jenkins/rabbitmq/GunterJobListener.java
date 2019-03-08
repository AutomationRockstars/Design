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
package com.automationrockstars.gunter.jenkins.rabbitmq;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.EventListener;
import com.automationrockstars.gunter.events.JobScheduled;
import com.automationrockstars.gunter.rabbit.Consumer;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;
import hudson.ExtensionPoint;
import hudson.model.AbstractProject;
import hudson.triggers.Trigger;
import jenkins.model.Jenkins;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class GunterJobListener extends Trigger<AbstractProject<?, ?>> implements ExtensionPoint, EventListener {

    public static final Logger LOG = Logger.getLogger(GunterJobListener.class.getName());
    private static Consumer consumer;

    public GunterJobListener() {
        LOG.info("Starting listener");
    }

    public static GunterJobListener get() {
        LOG.info("Getting instance");
        GunterJobListener listener = Jenkins.getInstance().getExtensionList(GunterJobListener.class).get(0);
        if (consumer == null &&
                ConfigLoader.config().getProperty(RabbitEventBroker.HOST_PROP) != null &&
                ConfigLoader.config().getString(RabbitEventBroker.HOST_PROP).length() > 0) {
            LOG.info("Registering listener for jobs using host " + ConfigLoader.config().getString(RabbitEventBroker.HOST_PROP));
            try {
                consumer = RabbitEventBroker.consumer("testing-jobs", getRoutingKey());
                consumer.registerListener(listener);
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Cannot create listener due to error", e);
                if (consumer != null) {
                    consumer.removeListener(listener);
                }
                consumer = null;
            }
        }
        return listener;
    }

    private static String getRoutingKey() {
        String defaultKey = "";
        try {
            defaultKey = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.config("IP address of host cannot be configured. Using empty as default key <<all messages will be accepted>>");
        }
        String result = ConfigLoader.config().getString("gunter.mq.key", defaultKey);
        LOG.config("Using " + result + " as messages routing key - only those jobs will be executed");
        return result;
    }

    public abstract void runJob(String project, Map<String, Object> params);

    @Override
    public void onEvent(Event event) {
        LOG.info("Received event " + event);
        if (event.getType() == EventType.JOB_SCHEDULED) {
            JobScheduled job = (JobScheduled) event;
            get().runJob(job.getProjectName(), job.getParameters());
        } else {
            LOG.info("Ignoring event " + event);
        }

    }


}
