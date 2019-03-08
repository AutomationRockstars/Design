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

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.StepDefinitionMatch;
import gherkin.formatter.Argument;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;


public class AllureCucumberPlugin extends AbstractCucumberPlugin implements Formatter, StepDefinitionReporter, Reporter {
    private static final GenericAllureStoryReporter reporter = new GenericAllureStoryReporter();
    private static final Logger LOG = LoggerFactory.getLogger(AllureCucumberPlugin.class);

    private static final ThreadLocal<String> featureUri = new ThreadLocal<>();
    private static final ThreadLocal<Match> currentStep = new ThreadLocal<>();

    private static String scenarioName(Scenario scenario) {
        String name = scenario.getName();
        if (Strings.isNullOrEmpty(name)) {
            name = scenario.getDescription().replaceAll("\\n", "").replaceAll("\\r", "");
        }
        return name;
    }

    private static String toName(Match match) {
        if (match instanceof StepDefinitionMatch) {
            Field stepField;
            try {
                stepField = StepDefinitionMatch.class.getDeclaredField("step");
                stepField.setAccessible(true);
                Step step = (Step) stepField.get(match);
                return step.getKeyword() + " " + step.getName();
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {

                e.printStackTrace();
            }

        }
        return (match != null && match.getLocation() != null) ? match.getLocation() : "unknown";
    }

    @Override
    public void uri(String uri) {
        featureUri.set(uri);
    }

    @Override
    public void feature(Feature feature) {
        LOG.info("Starting feature {}", feature.getName());
        reporter.beforeStory(feature.getName(), feature.getDescription(), featureUri.get());
    }

    ;

    //	@Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        LOG.info("Starting scenario {}", scenarioName(scenario));
        reporter.beforeScenario(scenarioName(scenario));
    }

    //	@Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        LOG.info("Finished scenario {}", scenarioName(scenario));
        reporter.afterScenario();
    }

    @Override
    public void done() {
        LOG.info("Generating report");
        GenericAllureStoryReporter.generateReport();
        LOG.info("Report generated. Test done");

    }

    @Override
    public void eof() {
        LOG.info("Story finished");
        reporter.afterStory();
    }

    @Override
    public void before(Match match, Result result) {
        LOG.info("BEFORE {} {} {}", match.getLocation(), match.getArguments(), result.getStatus());
    }

    @Override
    public void result(Result result) {
        switch (result.getStatus()) {
            case Result.PASSED:
                reporter.successful(toName(currentStep.get()));
                break;

            case Result.FAILED:
                reporter.failed(toName(currentStep.get()), result.getError());
                break;
            case "skipped":
                reporter.ignorable(toName(currentStep.get()));
                break;
        }

        LOG.info("Step {} finished with result {}", toName(currentStep.get()), result.getStatus());
    }

    @Override
    public void match(Match match) {
        Map<String, String> args = Maps.newHashMap();
        for (Argument arg : match.getArguments()) {
            args.put(arg.getOffset().toString(), arg.getVal());
        }
        reporter.example(args);
        reporter.beforeStep(toName(match));
        currentStep.set(match);
        LOG.info("Startin step {} {}", match.getLocation(), match.getArguments());

    }

}
