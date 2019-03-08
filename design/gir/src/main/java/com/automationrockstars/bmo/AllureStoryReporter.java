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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.automationrockstars.asserts.Asserts;
import com.automationrockstars.base.ConfigLoader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jbehave.core.model.*;
import org.jbehave.core.reporters.StoryReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.config.AllureModelUtils;
import ru.yandex.qatools.allure.events.*;
import ru.yandex.qatools.allure.model.Description;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.AllureReportBuilderException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class AllureStoryReporter implements StoryReporter {
    public static final ThreadLocal<String> currentScenario = new ThreadLocal<>();
    private static final ThreadLocal<String> currentSuite = new ThreadLocal<>();
    private static final ThreadLocal<String> currentSuiteName = new ThreadLocal<>();
    private static final ThreadLocal<String> originalThreadName = new ThreadLocal<String>();
    private static final ThreadLocal<Meta> currentMeta = new ThreadLocal<Meta>() {
        protected Meta initialValue() {
            return new Meta();
        }
    };
    private static final ThreadLocal<String> currentStep = new InheritableThreadLocal<>();
    @VisibleForTesting
    protected static CloseableHttpClient cl;
    static ThreadLocal<Map<String, String>> currentExample = new ThreadLocal<Map<String, String>>();
    private static Logger LOG = LoggerFactory.getLogger(AllureStoryReporter.class);
    private static Throwable stepFailed = null;
    private static Optional<File> resultsDir = null;

    public AllureStoryReporter() {
        LOG.info("instantiating");
        GenericAllureStoryReporter.cleanPreviousReport();
        setLogger();
        ConfigLoader.config().setProperty("assert.screenshot", false);
    }

    public static String storyName() {
        return currentSuiteName.get();
    }

    public static String scenarioName() {
        return currentScenario.get();
    }

    public static String stepName() {
        return currentStep.get();
    }

    public static void attachScreenshot(String name, byte[] content) {
        try {
            if (content != null) {
                Allure.LIFECYCLE.fire(new MakeAttachmentEvent(content, name, "image/png"));
            }
        } catch (Exception noScreenshot) {
            LOG.warn("Screenshot attaching failed due to {}", noScreenshot.toString());
        }
    }

    public static void attachScreenshot(String name) {
        attachScreenshot(name, Asserts.makeScreenshotIfPossible());
    }

    private static Properties populateProperty(String name, Properties initial) {
        if (ConfigLoader.config().containsKey(name)) {
            initial.setProperty(name, ConfigLoader.config().getString(name));
        }
        return initial;
    }

    @VisibleForTesting
    protected static void closeSession(String session) {
        try {
            LOG.info("Closing session {}", session);
            Class.forName("com.automationrockstars.design.gir.webdriver.DriverFactory").getMethod("closeSession", String.class).invoke(null, session);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            LOG.error("Cannot close session {} due to ", session, e);
        }
    }

    private static void generateProperties(String directory) {
        LOG.info("Generating properties for report");
        Properties environmentToShow = new Properties();
        environmentToShow = populateProperty("url", environmentToShow);
        environmentToShow = populateProperty("grid.url", environmentToShow);
        environmentToShow = populateProperty("webdriver.session", environmentToShow);
        GenericAllureStoryReporter.populateVideos(environmentToShow);
        LOG.info("Properties ready");
        try {
            environmentToShow.store(Files.newWriter(Paths.get(directory, "environment.properties").toFile(), Charset.defaultCharset()), "execution properties");
            LOG.info("Properties written");
        } catch (IOException e) {
            LOG.debug("Cannot generate properties");
        }
    }

    public static void generateReport() {
        String userSettings = String.format("%s/.m2/settings.xml", System.getProperty("user.home"));
        if (!Paths.get(userSettings).toFile().canRead()) {
            String globalSettings = String.format("%s/conf/settings.xml", ConfigLoader.config().getString("M2_HOME"));
            if (!Paths.get(globalSettings).toFile().canRead()) {
                LOG.error("Maven settings are not configured. Report cannot be generated");
            } else {
                try {
                    FileUtils.copyFile(Paths.get(globalSettings).toFile(), Paths.get(userSettings).toFile());
                } catch (IOException e) {
                    LOG.error("Cannot copy file due to ", e);

                }

            }
        }

        if (GenericAllureStoryReporter.getResultsDir().isPresent()) {
            generateProperties(GenericAllureStoryReporter.getResultsDir().get().getAbsolutePath());
            AllureReportBuilder bl;
            try {
                bl = new AllureReportBuilder("1.4.19", GenericAllureStoryReporter.getReportDir());
                bl.processResults(GenericAllureStoryReporter.getResultsDir().get());
                bl.unpackFace();
                URI report = Paths.get(GenericAllureStoryReporter.getReportDir().getAbsolutePath(), "index.html").toUri();
                LOG.info("Report generated to {}", report);
                if (ConfigLoader.config().containsKey("bdd.open.report")) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(report);
                        } catch (IOException e) {
                            LOG.warn("Report opening failed", e);
                        }
                    }
                }
            } catch (AllureReportBuilderException cantCreateReport) {
                LOG.error("Report is not generated due to ", cantCreateReport);
            }
        }

    }

    private void setLogger() {
        if (!LoggerFactory.getILoggerFactory().getClass().getName().contains("logback")) {
            return;
        }
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        final PatternLayoutEncoder ple = new PatternLayoutEncoder();
        String pattern = ConfigLoader.config().getString("allure.log.pattern", "%date|%logger{8}|%level|  %msg%n");
        if (ConfigLoader.config().containsKey("allure.log.detailed")) {
            pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n";
        }
        ple.setPattern(pattern);
        ple.setContext(lc);
        ple.start();
        final Logger rootLogger = LoggerFactory.getLogger("ROOT");
        if (rootLogger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) rootLogger;
            Appender<?> lame = null;
            Iterator<Appender<ILoggingEvent>> appenders = logger.iteratorForAppenders();
            while (appenders.hasNext()) {
                Appender<?> a = appenders.next();
                if (a instanceof AllureLogbackAppender) {
                    lame = a;
                }
            }
            if (lame == null) {
                AllureLogbackAppender<ILoggingEvent> appender = new AllureLogbackAppender<>();
                appender.setEncoder(ple);
                appender.setContext(lc);
                appender.start();
                logger.addAppender(appender);
            }
        }
    }

    @Override
    public void storyNotAllowed(Story story, String filter) {
        LOG.info("Story not allowed {} {} ", story, filter);

    }

    @Override
    public void storyCancelled(Story story, StoryDuration storyDuration) {

        LOG.info("Story cancelled {} {} ", story, storyDuration);

    }

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        String uuid = UUID.randomUUID().toString();
        currentSuite.set(uuid);
        TestSuiteStartedEvent storyEvent = new TestSuiteStartedEvent(uuid, story.getPath())
                .withDescription(new Description()
                        .withValue(story.getDescription().asString()))
                .withTitle(story.getName());
        currentSuiteName.set(story.getName());
        Allure.LIFECYCLE.fire(storyEvent);

    }

    @Override
    public void afterStory(boolean givenOrRestartingStory) {
        Allure.LIFECYCLE.fire(new TestSuiteFinishedEvent(currentSuite.get()));
    }

    @Override
    public void narrative(Narrative narrative) {
    }

    @Override
    public void lifecyle(Lifecycle lifecycle) {
    }

    @Override
    public void scenarioNotAllowed(Scenario scenario, String filter) {
        LOG.info("Scenario not allowed {} {}", scenario, filter);

    }

    @Override
    public void beforeScenario(String scenarioTitle) {
        LOG.info("Starting scenario {}", scenarioTitle);
        originalThreadName.set(Thread.currentThread().getName());
        currentScenario.set(scenarioTitle);
        Thread.currentThread().setName(scenarioTitle);
        stepFailed = null;
        TestCaseStartedEvent testCase = new TestCaseStartedEvent(currentSuite.get(), scenarioTitle);
        if (!Strings.isNullOrEmpty(currentMeta.get().getProperty("feature"))) {
            testCase.withLabels(AllureModelUtils.createFeatureLabel(currentMeta.get().getProperty("feature")));
        }
        if (!Strings.isNullOrEmpty(currentMeta.get().getProperty("story"))) {
            testCase.withLabels(AllureModelUtils.createFeatureLabel(currentMeta.get().getProperty("story")));
        }
        Allure.LIFECYCLE.fire(testCase);
    }

    @Override
    public void scenarioMeta(Meta meta) {
        currentMeta.set(meta);
        String feature = meta.getProperty("feature");
        String story = meta.getProperty("story");
        LOG.info("Scenario in feature {} and story {}", feature, story);

    }

    @Override
    public void afterScenario() {
        Thread.currentThread().setName(originalThreadName.get());
        if (stepFailed != null) {
            Allure.LIFECYCLE.fire(new TestCaseFailureEvent().withThrowable(stepFailed));
        }
        Allure.LIFECYCLE.fire(new TestCaseFinishedEvent());
    }

    @Override
    public void givenStories(GivenStories givenStories) {
        LOG.info("Given stories {}", givenStories);

    }

    @Override
    public void givenStories(List<String> storyPaths) {
        LOG.info("Given stories {}", storyPaths);

    }

    @Override
    public void beforeExamples(List<String> steps, ExamplesTable table) {
        LOG.info("before examples {} {}", steps, table);

    }

    @Override
    public void example(Map<String, String> tableRow) {
        currentExample.set(tableRow);
        LOG.info("example {}", tableRow);
        //		CsvListWriter csv = new CsvListWriter(Files.newWriter(Paths., charset), preference)
        //		Allure.LIFECYCLE.fire(new MakeAttachmentEvent(attachment, title, type));

    }

    @Override
    public void afterExamples() {
        LOG.info("After examples");

    }

    @Override
    public void beforeStep(String step) {

        List<String> stepParts = Splitter.onPattern("<|>").splitToList(step);
        StringBuilder dataStepName = new StringBuilder();
        for (String stepPart : stepParts) {
            if (currentExample.get() != null && currentExample.get().containsKey(stepPart)) {
                dataStepName.append("[").append(currentExample.get().get(stepPart)).append("]");
            } else {
                dataStepName.append(stepPart);
            }
        }
        Allure.LIFECYCLE.fire(new StepStartedEvent(dataStepName.toString()));
        if (!AllureLogbackAppender.isEmpty()) {
            AllureLogbackAppender.fire("before_" + step);
        }
        currentStep.set(step);
        LOG.info("before step {}", step);
    }

    @Override
    public void successful(String step) {
        AllureLogbackAppender.fire(step);
        Allure.LIFECYCLE.fire(new StepFinishedEvent());

    }

    @Override
    public void ignorable(String step) {
        if (ConfigLoader.config().getBoolean("bdd.ignorable.enabled", false)) {
//			Allure.LIFECYCLE.fire(new StepStartedEvent("IGNORED " + step));
            Allure.LIFECYCLE.fire(new StepCanceledEvent());
            Allure.LIFECYCLE.fire(new StepFinishedEvent());
        }

    }

    @Override
    public void pending(String step) {
        LOG.info("Pending {}", step);
        if (ConfigLoader.config().getBoolean("bdd.pending.enabled", false)) {
//			Allure.LIFECYCLE.fire(new StepStartedEvent("PENDING " + step));
            Allure.LIFECYCLE.fire(new StepCanceledEvent());
            Allure.LIFECYCLE.fire(new StepFinishedEvent());
        }
    }

    @Override
    public void notPerformed(String step) {
        if (ConfigLoader.config().getBoolean("bdd.not_performed.enabled", false)) {
            Allure.LIFECYCLE.fire(new StepStartedEvent("NOT PERFORMED " + step));
            Allure.LIFECYCLE.fire(new StepCanceledEvent());
            Allure.LIFECYCLE.fire(new StepFinishedEvent());
        }

    }

    @Override
    public void failed(String step, Throwable cause) {
        AllureLogbackAppender.fire(step);
        Throwable trueCause = cause.getCause();
        stepFailed = trueCause;
        attachScreenshot(step);
        Allure.LIFECYCLE.fire(new StepFailureEvent().withThrowable(trueCause));
        Allure.LIFECYCLE.fire(new StepFinishedEvent());

    }

    @Override
    public void failedOutcomes(String step, OutcomesTable table) {
        LOG.info("Failed outcomes {}, {}", step, table);

    }

    @Override
    public void restarted(String step, Throwable cause) {
        LOG.info("Restarted {} {}", step, cause);

    }

    @Override
    public void restartedStory(Story story, Throwable cause) {
        LOG.info("Restarted story {} {}", story, cause);

    }

    @Override
    public void dryRun() {
        LOG.info("dry run");

    }

    @Override
    public void pendingMethods(List<String> methods) {
        LOG.info("Pending methods {}", methods);

    }

}
