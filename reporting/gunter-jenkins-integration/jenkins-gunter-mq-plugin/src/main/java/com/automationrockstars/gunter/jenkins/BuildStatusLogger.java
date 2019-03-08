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

import com.automationrockstars.gunter.events.TestExecutionStart;
import com.automationrockstars.gunter.jenkins.rabbitmq.GlobalPublisher;
import hudson.console.ConsoleNote;
import hudson.model.BuildListener;
import hudson.model.Cause;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.util.AbstractTaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;


public class BuildStatusLogger extends AbstractTaskListener implements BuildListener {

    private static final java.util.logging.Logger LLOG = java.util.logging.Logger.getLogger("BuildStatusLogger");
    private static ThreadLocal<TestExecutionStart> startedId = new ThreadLocal<TestExecutionStart>();
    private final TaskListener delegate;
    Logger LOG = LoggerFactory.getLogger(BuildStatusLogger.class);
    public BuildStatusLogger(TaskListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void started(List<Cause> causes) {
        StringBuilder jobTitle = new StringBuilder();
        for (Cause cause : causes) {
            jobTitle.append(cause.getShortDescription() + ", ");
        }
        startedId.set(GlobalPublisher.jobStarted(jobTitle.toString()));

    }

    @Override
    public void finished(Result result) {
        GlobalPublisher.jobFinished(startedId.get(), result.toString());

    }


    @Override
    public PrintStream getLogger() {
        return delegate.getLogger();
    }

    @Override
    public void annotate(ConsoleNote ann) throws IOException {
        delegate.annotate(ann);

    }

    @Override
    public PrintWriter error(String msg) {
        return delegate.error(msg);
    }

    @Override
    public PrintWriter error(String format, Object... args) {
        return delegate.error(format, args);
    }

    @Override
    public PrintWriter fatalError(String msg) {
        return delegate.fatalError(msg);
    }

    @Override
    public PrintWriter fatalError(String format, Object... args) {
        return delegate.fatalError(format, args);
    }
}
