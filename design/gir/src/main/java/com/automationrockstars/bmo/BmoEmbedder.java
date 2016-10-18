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

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.SilentStepMonitor;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.StorySteps;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class BmoEmbedder extends Embedder {
	@Override
	public EmbedderControls embedderControls() {
		return new EmbedderControls()
				.doIgnoreFailureInStories(false)
				.doIgnoreFailureInView(false)
				.doVerboseFailures(true)
				.useStoryTimeouts(ConfigLoader.config().getString("bdd.story.timeout","600s"));
	}

	static String packageName = ConfigLoader.config().getString("bdd.steps.package");

	static {
		ConfigLoader.addEventListener(new ConfigurationListener() {
			
			@Override
			public void configurationChanged(ConfigurationEvent event) {
				if (event.getPropertyName().contains("bdd.steps.package")){
					stepClasses.clear();
				}
				
			}
		});
	}
	 public static Class<?> getCallerClass()  {
		 	StackTraceElement[] st = Thread.currentThread().getStackTrace();
	        StackTraceElement rawFQN = Iterables.find(Lists.newArrayList(st), new Predicate<StackTraceElement>() {

				@Override
				public boolean apply(StackTraceElement input) {
					return ! (input.toString().contains("automationrockstars")||
							input.toString().contains("internal") ||
							input.toString().startsWith("java.lang")
							);
				}
			});
	        try {
	        	String fqn = rawFQN.toString().split("\\(")[0].split("<")[0];
				return Class.forName(fqn.substring(0, fqn.lastIndexOf('.')));
			} catch (ClassNotFoundException e) {
				return null;
			}
	    }
	public static boolean dryRun = ConfigLoader.config().getBoolean("bdd.dry.run",false);
	private static Configuration config;
	private Class<?> codeLocation(){
		if (discoverSteps(packageName).isEmpty()){
			return getCallerClass();
		} else {
			return discoverSteps(packageName).get(0).getClass();
		}
	}
	@Override
	public Configuration configuration() {
		
		if (config == null){
			Class<? extends BmoEmbedder> embedderClass = this.getClass();
			config = new MostUsefulConfiguration()
					.useStoryLoader(new LoadFromClasspath(embedderClass.getClassLoader()))

					.useStoryReporterBuilder(new StoryReporterBuilder()
							.withReporters(new AllureStoryReporter())
							.withCodeLocation(CodeLocations.codeLocationFromClass(codeLocation()))
							.withDefaultFormats()
							.withFormats(CONSOLE, TXT, HTML, XML)
							.withCrossReference(new CrossReference()))
					.doDryRun(dryRun)
					.useParameterConverters(new ParameterConverters()
							.addConverters(
									new DateConverter(new SimpleDateFormat(ConfigLoader.config().getString("bdd.date.format","dd-MM-yyyy"))),
									new MapParameterConverter())) 
					.useStepPatternParser(new RegexPrefixCapturingPatternParser(
							ConfigLoader.config().getString("bdd.parameter.prefix","%"))) // use '%' instead of '$' to identify parameters
					.useStepMonitor(new SilentStepMonitor())
					.useFailureStrategy(new LogAndRethrowFailure());
		}
		return config;
	}

	private static final Logger LOG = LoggerFactory.getLogger(BmoEmbedder.class);

	@SuppressWarnings("rawtypes")
	private static final List stepClasses = Lists.newArrayList(); 
	@SuppressWarnings("unchecked" )
	private List<?> discoverSteps(String packageName){
		super.storyManager = null;
		if (stepClasses.isEmpty()){

			Reflections reflections = new Reflections(packageName);
			Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(StorySteps.class);
			for (Class<?> logic : annotated){
				try {
					LOG.info("Found steps class {}",logic);
					stepClasses.add(logic.newInstance());
				} catch (Exception e) {
					LOG.error("Injecting logic for {} failed due to {}",logic,e);
				}
			}
		}
		return stepClasses;
	}
	@Override
	public InjectableStepsFactory stepsFactory() {
		return new InstanceStepsFactory(configuration(), discoverSteps(packageName));
	}

	@Override
	public void runStoriesAsPaths(List<String> storyPaths){
		super.storyManager = null;
		super.runStoriesAsPaths(storyPaths);
	}


}
