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
import com.automationrockstars.bmo.junit.FakeTest;
import com.google.common.base.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import cucumber.api.CucumberOptions;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitOptions;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CucumberExecutor extends ParentRunner<FeatureRunner> {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberExecutor.class);
    private final List<FeatureRunner> children = new ArrayList<FeatureRunner>();
    private JUnitReporter jUnitReporter;
    private Runtime runtime;
    private RuntimeOptions runtimeOptions;
    private String features;

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws java.io.IOException                         if there is a problem
     * @throws org.junit.runners.model.InitializationError if there is another problem
     */
    public CucumberExecutor(Class<?> clazz) throws InitializationError, IOException {
        super(clazz);

        Assertions.assertNoCucumberAnnotatedMethods(clazz);
        ClassLoader classLoader = clazz.getClassLoader();
        if (clazz.getAnnotation(CucumberOptions.class) == null) {
            singleRunnerInit();
        } else {
            classInit(clazz);
        }
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        runtime = createRuntime(resourceLoader, classLoader, runtimeOptions);

        final List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader);
        jUnitReporter = new JUnitReporter(runtimeOptions.reporter(classLoader), runtimeOptions.formatter(classLoader),
                runtimeOptions.isStrict(), new JUnitOptions(Lists.newArrayList("--allow-started-ignored", "--filename-compatible-names")));
        addChildren(cucumberFeatures);

    }

    public CucumberExecutor() throws InitializationError, IOException {
        this(FakeTest.class);
    }

    public static String common(String a, String b) {
        List<String> base = Lists.newArrayList();
        List<String> x = Splitter.onPattern("/|\\\\").splitToList(a);
        List<String> y = Splitter.onPattern("/|\\\\").splitToList(b);
        for (int i = 0; i < x.size(); i++) {
            if (x.get(i).equals(y.get(i))) {
                base.add(x.get(i));
            } else break;
        }
        return Joiner.on("/").join(base);
    }

    public static String common(List<String> strings) {
        Preconditions.checkArgument(strings.size() > 0, "Cannot compare nothing");
        if (strings.size() == 1) return strings.get(0);
        String base = strings.get(0);
        for (int i = 1; i < strings.size(); i++) {
            base = common(base, strings.get(i));
        }
        return base;
    }

    private void classInit(Class<?> clazz) throws InitializationError, IOException {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        runtimeOptions = runtimeOptionsFactory.create();


    }

    /**
     * Create the Runtime. Can be overridden to customize the runtime or
     * backend.
     *
     * @param resourceLoader used to load resources
     * @param classLoader    used to load classes
     * @param runtimeOptions configuration
     * @return a new runtime
     * @throws InitializationError if a JUnit error occurred
     * @throws IOException         if a class or resource could not be loaded
     */
    protected Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader,
                                    RuntimeOptions runtimeOptions) throws InitializationError, IOException {
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    @Override
    public List<FeatureRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    //	private static final ExecutorService testRunners = Executors.newFixedThreadPool(ConfigLoader.config().getInt("parallel.runners",1));
    //	private static final LinkedBlockingQueue<Future<?>> featureRuns = Queues.newLinkedBlockingQueue();
    @Override
    protected void runChild(final FeatureRunner child, final RunNotifier notifier) {
        //		featureRuns.add(testRunners.submit(new Runnable() {
        //			@Override
        //			public void run() {
        child.run(notifier);
        //			}
        //		}));

    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        //		testRunners.shutdown();
        //		List<Throwable> errors = Lists.newArrayList();
        //		while (! featureRuns.isEmpty()){
        //			try {
        //				featureRuns.poll().get();
        //			} catch (InterruptedException | ExecutionException e) {
        //				errors.add(e);
        //				LOG.error("Error during execution",e);
        //			}
        //		}
        //		try {
        //			testRunners.awaitTermination(10, TimeUnit.MINUTES);
        //		} catch (InterruptedException e) {
        //			LOG.error("Error waiting for tests to complete",e);
        //		}
        //		LOG.warn("Errors during test execution {}",Joiner.on("\n").join(errors));
        jUnitReporter.done();
        jUnitReporter.close();
        runtime.printSummary();
    }

    private void addChildren(List<CucumberFeature> cucumberFeatures) throws InitializationError {
        for (CucumberFeature cucumberFeature : cucumberFeatures) {
            children.add(new FeatureRunner(cucumberFeature, runtime, jUnitReporter));
        }
    }

    public String tags() {
        return "";
    }

    public List<String> glue() {
        String defaultGlue = "stepDefinition/hiPlus,stepDefinition/common,";
        if (features().contains("motor")) {
            defaultGlue += "stepDefinition/motor";
        } else {
            defaultGlue += "stepDefinition/home,stepDefinition/mta";
        }
        LOG.info("Default glue is {}", defaultGlue);

        return Splitter.on(",").splitToList(ConfigLoader.config().getString("bdd.glue", defaultGlue));
    }

    public String features() {

        if (Strings.isNullOrEmpty(features)) {
            try {

                File featureDir = Paths.get(new File("").getAbsolutePath()).toFile();
                String[] DEFAULT_STORY_EXTENTIONS = new String[]{"feature"};
                String[] storyExtenstions = ConfigLoader.config().getStringArray("bdd.story.files");
                storyExtenstions = (storyExtenstions == null || storyExtenstions.length == 0) ? DEFAULT_STORY_EXTENTIONS : storyExtenstions;

                String[] argFilter = ConfigLoader.config().getStringArray("bdd.story.filter");
                final String[] filter = (argFilter == null || argFilter.length == 0) ? new String[]{"home"} : argFilter;
                LOG.info("Using filters {}", Arrays.toString(filter));

                Collection<File> allFeatures = FileUtils.listFiles(featureDir, storyExtenstions, true);
                List<String> result = Lists.newArrayList(Iterables.filter(Iterables.transform(allFeatures,
                        new Function<File, String>() {

                            @Override
                            public String apply(File input) {
                                return input.getAbsolutePath();
                            }
                        }), new Predicate<String>() {

                    @Override
                    public boolean apply(String input) {
                        boolean hasAll = true;
                        for (String partFilter : filter) {
                            hasAll = hasAll && input.contains(partFilter);
                        }
                        return hasAll && !input.contains("test-classes") && !input.contains("target") && input.contains("cucumber");
                    }
                }));
                features = common(result);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Cannot find features to execute. Please make sure cucumber folder contains files");
            }
        }
        LOG.info("Executing stories {}", features);
        Preconditions.checkArgument(!features.isEmpty(), "No story files has been found");
        return features;
    }

    private void singleRunnerInit() {
        List<String> runtimeArgs = Lists.newArrayList("-p",
                "html:target/cucumber", "-p", "json:target/features.json", "-p", "utilities.AllurePlugin",
                "-m");
        for (String gluePart : glue()) {
            runtimeArgs.add("-g");
            runtimeArgs.add(gluePart);
        }
        runtimeArgs.add(features());
        runtimeOptions = new RuntimeOptions(runtimeArgs);
    }

}

