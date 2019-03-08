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
package com.automationrockstars.gunter.allure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ru.yandex.qatools.allure.data.AllureReportGenerator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

class AllureModelBridge {

    public AllureEntity exec;
    private Map<String, AllureEntity> references = Maps.newHashMap();

    public void startRun(String name, Long startTime) {
        exec = new AllureEntity(Type.EXEC, name, startTime);
    }

    public void finishRun(Long endTime) {
        exec.finishTime = endTime;
    }

    public void addSuite(String id, String name, Long startTime) {
        AllureEntity suite = new AllureEntity(Type.SUITE, name, startTime);
        references.put(id, suite);
        exec.children.add(suite);
    }

    public void finish(String id, Long finishTime) {
        references.get(id).finishTime = finishTime;
    }

    public void addTest(String id, String suiteId, String testName, Long startTime) {
        AllureEntity test = new AllureEntity(Type.TEST, testName, startTime);
        references.get(suiteId).children.add(test);
        references.put(id, test);
    }

    public void addStep(String id, String testId, String name, Long startTime) {
        AllureEntity step = new AllureEntity(Type.STEP, name, startTime);
        references.get(testId).children.add(step);
        references.put(id, step);
    }

    public List<String> toXml() {
        List<String> result = Lists.newArrayList();
        for (AllureEntity suite : exec.children) {
            result.add(suiteToXml(suite));
        }
        return result;
    }

    private String suiteToXml(AllureEntity suite) {
        StringBuilder result = new StringBuilder(String.format("<ns2:test-suite xmlns:ns2=\"urn:model.allure.qatools.yandex.ru\" "
                + "start=\"%s\" stop=\"%s\">"
                + "\n<name>%s</name>\n", suite.startTime, suite.finishTime, suite.name));
        result.append("\n<test-cases>\n");
        for (AllureEntity test : suite.children) {
            result.append(testToXml(test)).append("\n");
        }
        result.append("</test-cases>\n<labels/>\n</ns2:test-suite>");
        return result.toString();
    }

    private String testToXml(AllureEntity test) {
        StringBuilder result = new StringBuilder(String.format("<test-case start=\"%s\" stop=\"%s\" status=\"%s\">\n"
                + "<name>%s</name>\n<steps>\n", test.startTime, test.finishTime, "passed", test.name));
        for (AllureEntity step : test.children) {
            result.append(stepToXml(step)).append("\n");
        }
        result.append("</steps>\n<attachments/>\n<labels/>\n</test-case>");
        return result.toString();
    }

    private String stepToXml(AllureEntity step) {
        StringBuilder result = new StringBuilder(String.format("<step start=\"%s\" stop=\"%s\" status=\"%s\">\n"
                + "<name>%s</name>\n<attachments/>\n<steps/>\n</step>", step.startTime, step.finishTime, "passed", step.name));
        return result.toString();
    }

    public void generate(Path in, Path out) {
        new AllureReportGenerator(in.toFile()).generate(out.toFile());
    }

    private enum Type {
        EXEC,
        SUITE,
        TEST,
        STEP
    }

    public class AllureEntity {
        public final String name;
        public final Long startTime;
        public final Type type;
        public Long finishTime;
        public List<AllureEntity> children = Lists.newArrayList();
        public AllureEntity(Type type, String name, Long startTime) {
            this.name = name;
            this.startTime = startTime;
            this.type = type;
        }
    }
}
