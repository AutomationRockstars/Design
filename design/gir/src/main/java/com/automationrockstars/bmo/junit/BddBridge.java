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
package com.automationrockstars.bmo.junit;

import com.automationrockstars.bmo.AllureStoryReporter;
import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import org.jbehave.core.junit.JUnitStories;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.List;

import static com.automationrockstars.bmo.JBehaveRunner.*;

/**
 * Extend this class to execute BDD scripts as JUnit tests
 * Please use the code below to finish execution
 * <pre>
 * <code>
 * <br>
 *    {@literal: @}Override<br>
 * 	protected List&lt;String&gt; storyPaths() {<br>
 * 		return super.features();<br>
 *    }<br>
 * </code>
 * </pre>
 */
@RunWith(JUnitReportingRunner.class)
public abstract class BddBridge extends JUnitStories {

    public BddBridge() {
        super();
        super.useEmbedder(embedder());
        super.useConfiguration(embedder().configuration());
        super.useStepsFactory(embedder().stepsFactory());
        JUnitReportingRunner.recommandedControls(embedder())
                .doIgnoreFailureInStories(false)
                .doIgnoreFailureInView(false)
                .doVerboseFailures(true)
                .doSkip(true);
    }

    @BeforeClass
    public static void prepareData() {
        transformData();
    }

    @AfterClass
    public static void cleanUp() {
        deleteTransformedFiles();
        AllureStoryReporter.generateReport();
    }

    /**
     * Method scanning for BDD scripts and providing it for execution
     *
     * @return BDD script files
     */
    public List<String> features() {
        return getFeatures();
    }


}
