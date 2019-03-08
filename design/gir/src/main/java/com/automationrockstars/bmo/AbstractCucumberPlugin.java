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

import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.StepDefinition;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.util.List;

public class AbstractCucumberPlugin implements Formatter, StepDefinitionReporter, Reporter {

    @Override
    public void before(Match match, Result result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void result(Result result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void after(Match match, Result result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void match(Match match) {
        // TODO Auto-generated method stub

    }

    @Override
    public void embedding(String mimeType, byte[] data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void write(String text) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stepDefinition(StepDefinition stepDefinition) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uri(String uri) {
        // TODO Auto-generated method stub

    }

    @Override
    public void feature(Feature feature) {
        // TODO Auto-generated method stub

    }

    @Override
    public void background(Background background) {
        // TODO Auto-generated method stub

    }

    @Override
    public void scenario(Scenario scenario) {
        // TODO Auto-generated method stub

    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        // TODO Auto-generated method stub

    }

    @Override
    public void examples(Examples examples) {
        // TODO Auto-generated method stub

    }

    @Override
    public void step(Step step) {
        // TODO Auto-generated method stub

    }

    @Override
    public void eof() {
        // TODO Auto-generated method stub

    }

    @Override
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
        // TODO Auto-generated method stub

    }

    @Override
    public void done() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
