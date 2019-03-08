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

import com.automationrockstars.design.gir.StorySteps;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@StorySteps
public class JBehaveSteps {

    String string;

    @Given("%string string")
    public void given(String string) {
        this.string = string;
    }

    @Then("expect %string")
    public void then(String string) {
        assertThat(this.string, equalTo(string));
    }

    @When("use %string string")
    public void when(String string) {
        this.string = string;
    }

}
