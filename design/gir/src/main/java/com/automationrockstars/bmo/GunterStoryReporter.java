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
package com.automationrockstars.bmo;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.events.*;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.automationrockstars.base.ConfigLoader.config;
import static com.automationrockstars.gunter.events.EventBus.fireEvent;
import static com.automationrockstars.gunter.events.EventFactory.*;

@RunReporter
public class GunterStoryReporter implements StoryReporter {

    private static final AtomicBoolean isConnected = new AtomicBoolean(false);
    private static final ThreadLocal<TestExecutionStart> start = new InheritableThreadLocal<>();
    private static final Logger LOG = LoggerFactory.getLogger(GunterStoryReporter.class);
    private static final ThreadLocal<TestSuiteStart> story = new InheritableThreadLocal<>();
    private static final ThreadLocal<TestCaseStart> scenario = new InheritableThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> example = new InheritableThreadLocal<>();
    private static final ThreadLocal<TestStepStart> step = new InheritableThreadLocal<>();

    private static String serialize(Event event) {
        Configuration gunterAttrs = ConfigLoader.config().subset("gunter.attr");
        LOG.info("Found Gunter attributes {}", ConfigLoader.logConfig(gunterAttrs));
        Set<String> attrNames = Sets.newHashSet();
        if (!gunterAttrs.isEmpty()) {
            for (String key : Lists.newArrayList(gunterAttrs.getKeys())) {

                if (!key.equalsIgnoreCase("onevent")) {
                    try {
                        LOG.info("Gunter using name {}", key);
                        if (key.contains(".")) {
                            key = key.split(".")[0];
                        }
                        attrNames.add(key);
                    } catch (IndexOutOfBoundsException e) {
                        LOG.error("Something wrong with Gunter attribute gunter.attr.{}", key, e.toString());
                    }
                }
            }

            LOG.info("Gunter atrributes {}", Joiner.on("\n").join(attrNames));
            for (String attrName : attrNames) {
                LOG.info("Gunter content {}", gunterAttrs.getString(attrName));
                LOG.info("Gunter trigger {}", gunterAttrs.getString(attrName + ".onevent"));

                if (gunterAttrs.getString(attrName + ".onevent").toUpperCase().equals(event.getType().toString())) {
                    LOG.info("Gunter attribute {} added {}", attrName, gunterAttrs.getString(attrName));
                    event.setAttribute(attrName, gunterAttrs.getString(attrName));
                }
            }
        }
        return toJson(event);
    }

    @Override
    public String name() {
        return "Guner Story Reporter";
    }

    @Override
    public void start() {
        isConnected.set(false);
        if (ConfigLoader.config().containsKey("rabbitmq.host")) {
            try {
                RabbitEventBroker.init();
                isConnected.set(true);
            } catch (IllegalStateException e) {
                isConnected.set(false);
            }
            if (isConnected.get()) {
                start.set(createExecutionStart(config().getString("execution.name", config().getString("bdd.story.filter"))));
                fireEvent(serialize(start.get()));
            }
        }
    }

    @Override
    public void finish() {
        if (isConnected.get()) {
            fireEvent(serialize(createExecutionFinish(start.get(), "FINISHED")));
        }

    }

    @Override
    public void beforeStory(String name, String description, String path) {
        if (isConnected.get()) {
            story.set(createSuiteStart(start.get(), name));
            fireEvent(serialize(story.get()));
        }
    }

    @Override
    public void afterStory() {
        if (isConnected.get()) {
            fireEvent(serialize(createSuiteFinish(story.get(), "FINISHED")));
        }
    }

    @Override
    public void beforeScenario(String scenarioTitle) {
        if (isConnected.get()) {
            scenario.set(createTestCaseStart(story.get(), scenarioTitle));
            fireEvent(serialize(scenario.get()));
        }
    }

    @Override
    public void afterScenario() {
        if (isConnected.get()) {
            fireEvent(serialize(createTestCaseFinish(scenario.get(), "FINISHED")));
        }

    }

    @Override
    public void example(Map<String, String> tableRow) {
        example.set(tableRow);

    }

    @Override
    public void beforeStep(String stepName) {
        if (isConnected.get()) {
            step.set(createTestStepStart(scenario.get(), stepName));
            fireEvent(serialize(step.get()));
        }
    }

    @Override
    public void successful(String stepName) {
        if (isConnected.get()) {
            fireEvent(serialize(createTestStepFinish(step.get(), "SUCCESS")));
        }
    }

    @Override
    public void ignorable(String stepName) {
        if (isConnected.get()) {
            fireEvent(serialize(createTestStepFinish(step.get(), "IGNORED")));
        }
    }

    @Override
    public void pending(String stepName) {
        if (isConnected.get()) {
            fireEvent(serialize(createTestStepFinish(step.get(), "PENDING")));
        }
    }

    @Override
    public void notPerformed(String stepName) {
        if (isConnected.get()) {
            fireEvent(serialize(createTestStepFinish(step.get(), "NOT PERFORMED")));
        }

    }

    @Override
    public void failed(String stepName, Throwable cause) {
        if (isConnected.get()) {

            ByteArrayOutputStream content = new ByteArrayOutputStream();
            PrintStream print = new PrintStream(content);
            cause.printStackTrace(print);
            String throwable = content.toString();
            try {
                content.close();
                print.close();
            } catch (IOException e) {
            }
            TestStepFinish finish = createTestStepFinish(step.get(), "FAILED", cause.toString());
            fireEvent(serialize(finish));
        }
    }


    @Override
    public void attach(byte[] attachment, String title, String mimeType) {
        if (isConnected.get()) {
            fireEvent(serialize(createAttachment(step.get(), mimeType, title, attachment)));
        }
    }

    public int order() {
        return 1000;
    }

}
