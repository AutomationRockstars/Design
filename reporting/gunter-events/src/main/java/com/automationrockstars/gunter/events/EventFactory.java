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
package com.automationrockstars.gunter.events;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.environment.EnvironmentBroken;
import com.automationrockstars.gunter.events.environment.EnvironmentUnderTest;
import com.automationrockstars.gunter.events.environment.EnvironmentWorking;
import com.automationrockstars.gunter.events.environment.impl.EnvironmentBrokenImpl;
import com.automationrockstars.gunter.events.environment.impl.EnvironmentUnderTestImpl;
import com.automationrockstars.gunter.events.environment.impl.EnvironmentWorkingImpl;
import com.automationrockstars.gunter.events.impl.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class EventFactory {

    private static final String DEFAULT = "DEFAULT";


    private static final Logger LOG = LoggerFactory.getLogger(EventFactory.class);

    public static TestExecutionStart createExecutionStart(String executionName) {
        if (executionName == null) {
            executionName = DEFAULT;
        }
        TestExecutionStart result = new TestExecutionStartImpl();
        result.setExecutionName(executionName);
        return result;
    }

    public static Commit createCommit(String parent, Map<String, String> content) {
        Commit result = new CommitImpl(parent);
        result.setContent(content);
        return result;
    }

    public static TestExecutionStart createExecutionStart(String executionName, Map<String, Object> parameters) {
        TestExecutionStart result = createExecutionStart(executionName);
        if (parameters != null) {
            result.setParameters(parameters);
        }
        return result;
    }

    public static Sample createSample(String host, String type, Map<String, Number> sample) {
        Sample result = new SampleImpl();
        result.setHost(host);
        result.setSampleType(type);
        result.setSample(sample);
        return result;
    }

    public static TestExecutionFinish createExecutionFinish(TestExecutionStart parent, String status) {
        TestExecutionFinish output = new TestExecutionFinishImpl(parent);
        if (status == null) {
            status = DEFAULT;
        }
        output.setStatus(status);
        return output;
    }

    public static TestSuiteStart createSuiteStart(TestExecutionStart parent, String suiteName) {
        TestSuiteStart output = new TestSuiteStartImpl(parent.getId());
        if (suiteName == null) {
            suiteName = DEFAULT;
        }
        output.setTestSuiteName(suiteName);
        return output;
    }

    public static TestSuiteFinish createSuiteFinish(TestSuiteStart parent, String status) {
        TestSuiteFinish output = new TestSuiteFinishImpl(parent);
        if (status == null) {
            status = DEFAULT;
        }
        output.setStatus(status);
        return output;
    }

    public static TestCaseStart createTestCaseStart(TestSuiteStart parent, String testCaseName) {
        TestCaseStart output = new TestCaseStartImpl(parent.getId());
        if (testCaseName == null) {
            testCaseName = DEFAULT;
        }
        output.setName(testCaseName);
        return output;
    }

    public static TestCaseFinish createTestCaseFinish(TestCaseStart parent, String status) {
        TestCaseFinish output = new TestCaseFinishImpl(parent);
        if (status == null) {
            status = DEFAULT;
        }
        output.setStatus(status);
        return output;
    }

    public static TestStepStart createTestStepStart(TestCaseStart parent, String testStepName) {
        TestStepStart output = new TestStepStartImpl(parent.getId());
        if (testStepName == null) {
            testStepName = DEFAULT;
        }
        output.setName(testStepName);
        return output;
    }

    public static TestStepFinish createTestStepFinish(TestStepStart parent, String status) {
        TestStepFinish output = new TestStepFinishImpl(parent.getId());
        if (status == null) {
            status = DEFAULT;
        }
        output.setStatus(status);
        return output;
    }

    public static TestStepFinish createTestStepFinish(TestStepStart parent, String status, String cause) {
        TestStepFinish output = createTestStepFinish(parent, status);
        output.setCause(cause);
        return output;
    }

    public static Log createLog(Event parent, String level, String origin, String message) {
        Log output = new LogImpl(parent.getId());
        output.log(level, origin, message);
        return output;
    }

    public static Attachment createAttachment(Event parent, String mimeType, String title, byte[] content) {
        Attachment output = new AttachmentImpl(parent.getId());
        output.attach(mimeType, title, content);
        return output;
    }

    public static Action createAction(Event parent, String actionName, String element) {

        Action output = new ActionImpl((parent == null) ? null : parent.getId());
        output.setAction(actionName, element);
        return output;
    }


    public static JobScheduled createJobScheduled(String projectName, Map<String, Object> parameters) {
        JobScheduled result = new JobScheduledImpl(projectName);
        if (parameters != null) {
            result.setParameters(parameters);
        }
        return result;
    }


    public static EnvironmentUnderTest createEnvironmentUnderTest(String parentId, String name) {
        return new EnvironmentUnderTestImpl(parentId, name);
    }

    public static EnvironmentBroken createEnvironmentBroken(String parentId, String name, String cause) {
        return new EnvironmentBrokenImpl(parentId, name, cause);
    }

    public static EnvironmentWorking createEnvironmentWorking(String parentId, String name) {
        return new EnvironmentWorkingImpl(parentId, name);
    }

    private static Class<? extends Event> getClass(String jsonString) {
        Iterator<String> result = Iterators.filter(Splitter.on(",").split(jsonString).iterator(), new Predicate<String>() {

            public boolean test(String part){
                return apply(part);
            }
            @Override
            public boolean apply(String part) {
                return part.contains("type");
            }
        });
        String typeLine = result.next().split(":")[1];
        String eventName = CharMatcher.noneOf(" \":\n\r{}").retainFrom(typeLine);
        eventName = CharMatcher.javaLetterOrDigit().or(CharMatcher.anyOf("_")).retainFrom(eventName);
        return EventImplUtils.getClassForType(EventType.valueOf(eventName));
    }


    @SuppressWarnings("unchecked")
    public static <T extends Event> T fromJson(String jsonString) {
        Class<? extends Event> eventClass = getClass(jsonString);
        return (T) fromJson(jsonString, eventClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> T fromJson(String jsonString, Class<T> klas) {
        ObjectMapper mapper = new ObjectMapper();
        Event output = null;
        try {
            output = mapper.readValue(jsonString.getBytes(), klas);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) output;
    }

    public static String toJson(Event event) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(DecoratorFinder.decorate(event));
        } catch (JsonProcessingException e) {
            LOG.error("Event {} cannot be put to json due to {}", event, e.toString());
        }
        return null;

    }


}

