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

import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

public class CompositeStoryReporterTest {

    @Test
    public void should_loadAlReportersExceptSelf() {
        CompositeStoryReporter.load();
        assertThat(CompositeStoryReporter.subReporters(), hasSize(greaterThan(0)));
        assertThat(CompositeStoryReporter.subReporters(), not(contains(instanceOf(CompositeStoryReporter.class))));
    }

}
