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


import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.TestExecutionStart;
import com.automationrockstars.gunter.jenkins.rabbitmq.GlobalPublisher;
import com.google.common.collect.Maps;
import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@Extension
public class GunterRunListener extends RunListener<Run> {

    private static final BuildStatusLogger logger = new BuildStatusLogger(null);
    private static Logger LOG = Logger.getLogger(GunterRunListener.class.getName());
    private static ConcurrentMap<Run, TestExecutionStart> parentEvents = Maps.newConcurrentMap();

    public GunterRunListener() {
        super();
        LOG.info("Gunter run listener starting");
    }

    private static Map<String, Object> parameters(Run r) {
        Map<String, Object> result = Maps.newHashMap();
        ParametersAction params = r.getAction(ParametersAction.class);
        if (params != null) {
            for (ParameterValue param : params.getParameters()) {
                String paramString = param.toString();
                if (paramString.contains("=")) {
                    String[] stringParts = paramString.split("=");
                    result.put(stringParts[0], stringParts[1]);
                }
            }
        }
        return result;
    }

    private static final String jobName(Run<?, ?> r) {
        StringBuilder result = new StringBuilder(r.getFullDisplayName()).append("\n");
        for (Cause cause : r.getCauses()) {
            result.append(cause.getShortDescription()).append(", ");
        }


        return result.toString();
    }

    public void onStarted(Run r, TaskListener listener) {
        TestExecutionStart start = EventFactory.createExecutionStart(jobName(r), parameters(r));
        parentEvents.put(r, start);
        GlobalPublisher.jobStarted(EventFactory.toJson(start));
    }

    public void onCompleted(Run r, @Nonnull TaskListener listener) {
        GlobalPublisher.jobFinished(parentEvents.get(r), r.getResult().toString());
    }

}
