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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.asserts.Asserts;
import com.automationrockstars.base.ConfigLoader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.config.AllureModelUtils;
import ru.yandex.qatools.allure.events.MakeAttachmentEvent;
import ru.yandex.qatools.allure.events.StepCanceledEvent;
import ru.yandex.qatools.allure.events.StepFailureEvent;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;
import ru.yandex.qatools.allure.events.TestCaseFailureEvent;
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent;
import ru.yandex.qatools.allure.events.TestCaseStartedEvent;
import ru.yandex.qatools.allure.events.TestSuiteFinishedEvent;
import ru.yandex.qatools.allure.events.TestSuiteStartedEvent;
import ru.yandex.qatools.allure.model.Description;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.AllureReportBuilderException;

@RunReporter
public class GenericAllureStoryReporter implements StoryReporter {
	private static Logger LOG = LoggerFactory.getLogger(GenericAllureStoryReporter.class); 
	
	private static final AtomicBoolean finished = new AtomicBoolean(false);
	private void setLogger(){
		if ( ! LoggerFactory.getILoggerFactory().getClass().getName().contains("logback")){
			return;
		}
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		final PatternLayoutEncoder ple = new PatternLayoutEncoder();
		String pattern = ConfigLoader.config().getString("allure.log.pattern","%date|%logger{8}|%level|  %msg%n");
		if (ConfigLoader.config().containsKey("allure.log.detailed")){
			pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n";
		}
		ple.setPattern(pattern);
		ple.setContext(lc);
		ple.start();
		final Logger rootLogger = LoggerFactory.getLogger("ROOT");
		if (rootLogger instanceof ch.qos.logback.classic.Logger){
			ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)rootLogger;
			Appender<?> lame =null;
			Iterator<Appender<ILoggingEvent>> appenders = logger.iteratorForAppenders();
			while (appenders.hasNext()){
				Appender<?> a = appenders.next();
				if (a instanceof AllureLogbackAppender) {
					lame = a;
				}
			}
			if (lame == null){
				AllureLogbackAppender<ILoggingEvent> appender = new AllureLogbackAppender<>();
				appender.setEncoder(ple);
				appender.setContext(lc);
				appender.start();
				logger.addAppender(appender);
			}
		}
	}

	public GenericAllureStoryReporter() {
		LOG.info("instantiating");

	}

	private static final ThreadLocal<String> currentSuite = new ThreadLocal<>();
	private static final ThreadLocal<String> currentSuiteName = new ThreadLocal<>();
	public static String storyName(){
		return currentSuiteName.get();
	}

	public void beforeStory(String name, String description, String path ) {
		String uuid = UUID.randomUUID().toString();
		currentSuite.set(uuid);
		TestSuiteStartedEvent storyEvent = new TestSuiteStartedEvent(uuid, path)
				.withDescription(new Description()
						.withValue(description))
				.withTitle(name);
		currentSuiteName.set(name);
		Allure.LIFECYCLE.fire(storyEvent);

	}


	public void afterStory() {
		Allure.LIFECYCLE.fire(new TestSuiteFinishedEvent(currentSuite.get()));
	}


	public static final ThreadLocal<String> currentScenario = new ThreadLocal<>();
	private static final ThreadLocal<String> originalThreadName = new ThreadLocal<String>();


	public void beforeScenario(String scenarioTitle) {
		LOG.info("Starting scenario {}",scenarioTitle);
		originalThreadName.set(Thread.currentThread().getName());
		currentScenario.set(scenarioTitle);
		Thread.currentThread().setName(CharMatcher.JAVA_DIGIT.retainFrom(originalThreadName.get()) + "|" + scenarioTitle);
		stepFailed.set(null);
		TestCaseStartedEvent testCase = new TestCaseStartedEvent(currentSuite.get(), scenarioTitle);
		if (! Strings.isNullOrEmpty(currentMeta.get().getProperty("feature"))){
			testCase.withLabels(AllureModelUtils.createFeatureLabel(currentMeta.get().getProperty("feature")));
		}
		if (! Strings.isNullOrEmpty(currentMeta.get().getProperty("story"))){
			testCase.withLabels(AllureModelUtils.createFeatureLabel(currentMeta.get().getProperty("story")));
		}
		Allure.LIFECYCLE.fire(testCase);
	}

	private static final ThreadLocal<Properties> currentMeta = new ThreadLocal<Properties>(){
		protected Properties initialValue(){
			return new Properties();
		}
	};


	public void scenarioMeta(Properties meta) {
		currentMeta.set(meta);
		String feature = meta.getProperty("feature");
		String story = meta.getProperty("story");
		LOG.info("Scenario in feature {} and story {}",feature,story);

	}


	public void afterScenario() {
		Thread.currentThread().setName(originalThreadName.get());
		if (stepFailed.get() != null){
			Allure.LIFECYCLE.fire(new TestCaseFailureEvent().withThrowable(stepFailed.get()));
		}
		Allure.LIFECYCLE.fire(new TestCaseFinishedEvent());
	}




	static ThreadLocal<Map<String,String>> currentExample = new ThreadLocal<Map<String,String>>();

	public void example(Map<String, String> tableRow) {
		currentExample.set(tableRow);
		LOG.info("example {}",tableRow);
		//		CsvListWriter csv = new CsvListWriter(Files.newWriter(Paths., charset), preference)
		//		Allure.LIFECYCLE.fire(new MakeAttachmentEvent(attachment, title, type));

	}




	public void beforeStep(String step) {
		List<String> stepParts = Splitter.onPattern("<|>").splitToList(step);
		StringBuilder dataStepName = new StringBuilder();
		for (String stepPart : stepParts){
			if (currentExample.get() != null && currentExample.get().containsKey(stepPart)){
				dataStepName.append("[").append(currentExample.get().get(stepPart)).append("]");
			} else {
				dataStepName.append(stepPart);
			}
		}
		Allure.LIFECYCLE.fire(new StepStartedEvent(dataStepName.toString()));
		if (! AllureLogbackAppender.isEmpty()){
			AllureLogbackAppender.fire("before_"+step);
		}
		LOG.info("before step {}",step);
	}


	public void successful(String step) {
		AllureLogbackAppender.fire(step);
		Allure.LIFECYCLE.fire(new StepFinishedEvent());

	}


	public void ignorable(String step) {
		//TODO how is it in jbehave
		//		Allure.LIFECYCLE.fire(new StepStartedEvent("IGNORED " + step));
		Allure.LIFECYCLE.fire(new StepCanceledEvent());
		Allure.LIFECYCLE.fire(new StepFinishedEvent());

	}


	public void pending(String step) {
		LOG.info("Pending {}",step);
		//		Allure.LIFECYCLE.fire(new StepStartedEvent("PENDING " + step));
		Allure.LIFECYCLE.fire(new StepCanceledEvent());
		Allure.LIFECYCLE.fire(new StepFinishedEvent());
	}


	public void notPerformed(String step) {
		//		Allure.LIFECYCLE.fire(new StepStartedEvent("NOT PERFORMED " + step));
		Allure.LIFECYCLE.fire(new StepCanceledEvent());
		Allure.LIFECYCLE.fire(new StepFinishedEvent());

	}

	private static final ThreadLocal<Throwable> stepFailed = new ThreadLocal<>();


	public void failed(String step, Throwable cause) {
		AllureLogbackAppender.fire(step);
		Throwable trueCause  = cause;
		if (cause instanceof InvocationTargetException){
			trueCause = (cause.getCause()!=null)?cause.getCause():cause;
		}

		stepFailed.set(trueCause);
		attachScreenshot(step);
		Allure.LIFECYCLE.fire(new StepFailureEvent().withThrowable(trueCause));
		Allure.LIFECYCLE.fire(new StepFinishedEvent());

	}

	private void attachScreenshot(String name){
		try {
			LOG.info("Attaching screenshot to {}",name);		
			byte[] screen = Asserts.makeScreenshotIfPossible(); 
			if (screen != null){
				attach(screen, name, "image/png");
			}		
		} catch (Exception noScreenshot) {
			LOG.warn("Screenshot attaching failed due to {}",noScreenshot.toString());
		}
	}



	private static Properties populateProperty(String name, Properties initial){
		if (ConfigLoader.config().containsKey(name)){
			initial.setProperty(name, ConfigLoader.config().getString(name));
		}
		return initial;
	}
	
	@VisibleForTesting
	protected static CloseableHttpClient cl;



	@VisibleForTesting
	protected static void closeSession(String session){
		try {
			LOG.info("Closing session {}",session);
			Class.forName("com.automationrockstars.design.gir.webdriver.DriverFactory").getMethod("closeSession",String.class).invoke(null, session);
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOG.error("Cannot close session {} due to ",session,e);
		}
	}
	private static boolean canGetVideo(final String link){
		CloseableHttpResponse resp = null;
		try {
			resp = cl.execute(new HttpGet(link));
			if ( resp.getStatusLine().getStatusCode() != 200){
				throw new IllegalArgumentException("Negative response from server " + resp.getStatusLine());
			}
			return true;
		} catch (Throwable t){
			LOG.debug("Video {} cannot be fetched due to {}",link,t.getMessage());
			return false;
		} finally {
			if (resp!=null){
				try {
					resp.close();
				} catch (IOException ignore) {
				}
			}
		}
	}
	
	@VisibleForTesting
	protected static Set<Map<String,String>> minimize(){
		Set<?> videos = Sets.newHashSet(ConfigLoader.config().getList("webdriver.videos",Lists.newArrayList()));
		List<Map<String,String>> videosData = Lists.newArrayList();
		Set<Map<String,String>> result = Sets.newTreeSet(new Comparator<Map<String,String>>(){

			@Override
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				String key1 = o1.keySet().iterator().next();
				String key2 = o2.keySet().iterator().next();
				return key1.compareTo(key2);
			}});
		Set<String> titles = Sets.newTreeSet(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		for (Object video : videos){
			@SuppressWarnings("unchecked")
			Map.Entry<String, String> videoData = ((Map<String, String>) video).entrySet().iterator().next();
			closeSession(videoData.getValue().replaceAll(".*/download_video/", "").replaceAll(".mp4", ""));
			titles.add(videoData.getKey().split("->")[0]);
			videosData.add(Collections.singletonMap(videoData.getKey(), videoData.getValue()));
		}
		
		if (titles.size() != videos.size()){
			for (final String title : titles){
				FluentIterable<Map<String,String>> videosForScenario = FluentIterable.from(videosData).filter(new Predicate<Map<String,String>>() {

					@Override
					public boolean apply(Map<String, String> input) {
						return input.keySet().iterator().next().contains(title+"->");
					}
				});
				if (videosForScenario.size() == 1){
					result.add(Collections.singletonMap(title+"::",videosForScenario.first().get().values().iterator().next()));
				} else {
					result.addAll(videosForScenario.toList());
				}
			}
		}
		LOG.info("Preparing to download videos for:\n{}",Joiner.on("\n").join(result));
		return result;
	}
	public static void populateVideos(Properties initial){	
		Set<Map<String,String>> videos = minimize();
		if (videos.isEmpty()){
			initial = populateProperty("webdriver.video",initial);
		} else {
			cl = HttpClients.createDefault();
			
			for (Map<String,String> video : videos){
				Map.Entry<String, String> videoData =  video.entrySet().iterator().next();
				populateVideo(initial, videoData.getValue(), videoData.getKey());
			}
			try {
				cl.close();
			} catch (IOException e) {
			}
		}
	}
	@VisibleForTesting
	public static void populateVideo(Properties initial, String link, String title){
		try {
			LOG.debug("Trying to download {}",link);
			if (canGetVideo(link)){
				Paths.get("target/allure-report/data/").toFile().mkdirs();
				String videoTitle = link.split("/")[link.split("/").length-1].replace(".mp4", "");
				Path ying = Paths.get("target/allure-report/data/"+videoTitle+".mp4.1");
				Path yang = Paths.get("target/allure-report/data/"+videoTitle+".mp4.2");
				Path dest = Paths.get("target/allure-report/data/"+videoTitle+".mp4");
				do {
					CloseableHttpResponse videoResponse = cl.execute(new HttpGet(link));
					java.nio.file.Files.copy(videoResponse.getEntity().getContent(), ying, StandardCopyOption.REPLACE_EXISTING);
					videoResponse.close();
					videoResponse = cl.execute(new HttpGet(link));
					java.nio.file.Files.copy(videoResponse.getEntity().getContent(), yang, StandardCopyOption.REPLACE_EXISTING);
					videoResponse.close();
				} while (java.nio.file.Files.size(yang) != java.nio.file.Files.size(yang));
				FileUtils.moveFile(ying.toFile(), dest.toFile());
				FileUtils.forceDelete(yang.toFile());
				initial.setProperty(title, "<a href=\"data/"+videoTitle+".mp4\"><video id=\""+title+"\" width=\"160\" height=\"88\" autoplay=\"true\" controls=\"true\">"+
						"<source src=\"data/"+videoTitle+".mp4\" type=\"video/mp4\">"+
						"Your browser does not support HTML5 video.</video></a>");
				LOG.info("Video downloaded to {}",dest);
			}
		} catch (UnsupportedOperationException | IOException e) {
			LOG.error("Video cannot be added {}",e.getMessage());
		}
	}
	
	private static void generateProperties(String directory){
		LOG.info("Generating properties for report");
		Properties environmentToShow = new Properties();
		environmentToShow = populateProperty("url", environmentToShow);
		environmentToShow = populateProperty("grid.url", environmentToShow);
		environmentToShow = populateProperty("webdriver.session", environmentToShow);
		populateVideos(environmentToShow);
		LOG.info("Properties ready");
		try {
			environmentToShow.store(Files.newWriter(Paths.get(directory,"environment.properties").toFile(), Charset.defaultCharset()), "execution properties");
			LOG.info("Properties written");
		} catch (IOException e) {
			LOG.debug("Cannot generate properties");
		}
	}

	private static Optional<File> resultsDir = null;

	private static Optional<File> getResultsDir(){
		Optional<File> dire = Optional.absent();
		if (resultsDir == null || ! resultsDir.isPresent()){
			Collection<File> dirs = FileUtils.listFilesAndDirs(new File(new File("").getAbsolutePath()), FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE); 
			dire = Iterables.tryFind(dirs, new Predicate<File>() {

				@Override
				public boolean apply(File input) {
					return input.getName().endsWith("allure-results");
				}
			});
			resultsDir = dire;
		}
		return resultsDir;
	}
	private static File reportDir = null;
	private static File getReportDir(){
		if (reportDir == null){
			if (getResultsDir().isPresent())
				reportDir = Paths.get(getResultsDir().get().getParent(),"allure-report").toFile();
		}
		return reportDir;
	}
	public static void cleanPreviousReport(){
		if (getResultsDir().isPresent() && ! ConfigLoader.config().getBoolean("allure.merge",false)){
			try {
				FileUtils.forceDelete(getReportDir());
			} catch (IOException e){
				LOG.warn("Deleting previous report failed");
			}
			try {
				FileUtils.forceDelete(getResultsDir().get());
			} catch (IOException e) {
				LOG.warn("Deleting previous results failed");
			}
		}
	}
	public static void generateReport() {
		String userSettings = String.format("%s/.m2/settings.xml", System.getProperty("user.home"));
		String[] ext = new String[]{"xml"};
		Optional<File> specialPom = Iterables.tryFind(FileUtils.listFiles(new File("").getAbsoluteFile(), ext, true), new Predicate<File>() {

			@Override
			public boolean apply(File input) {
				return input.getName().equals("settings.xml");
			}
		}); 
		if (specialPom.isPresent() && ConfigLoader.config().getBoolean("update.m2.settings",false)){
			try {
				FileUtils.copyFile(specialPom.get(), new File(userSettings));
			} catch (IOException e) {
				LOG.error("Cannot copy special pom from {}",specialPom.get());
			}
		}


		if (! Paths.get(userSettings).toFile().canRead()){
			String globalSettings = String.format("%s/conf/settings.xml", ConfigLoader.config().getString("M2_HOME"));
			if (! Paths.get(globalSettings).toFile().canRead()){
				LOG.error("Maven settings are not configured. Report cannot be generated");
			} else {
				try {
					FileUtils.copyFile(Paths.get(globalSettings).toFile(), Paths.get(userSettings).toFile());
				} catch (IOException e) {
					LOG.error("Cannot copy file due to ",e);

				}

			}
		}
		if (getResultsDir().isPresent()){

			generateProperties(getResultsDir().get().getAbsolutePath());
			AllureReportBuilder bl;
			try {
				bl = new AllureReportBuilder("1.4.19", getReportDir());
				bl.processResults(getResultsDir().get());
				bl.unpackFace();
				URI report = Paths.get(getReportDir().getAbsolutePath(),"index.html").toUri();
				LOG.info("Report generated to {}",report);
				if (ConfigLoader.config().containsKey("bdd.open.report")){
					if(Desktop.isDesktopSupported())
					{
						try {
							Desktop.getDesktop().browse(report);
						} catch (IOException e) {
							LOG.warn("Report opening failed",e);
						}
					}
				}
			} catch (AllureReportBuilderException  cantCreateReport) {
				LOG.error("Report is not generated due to ",cantCreateReport);
			}
		}

	}

	@Override
	public void start() {
		cleanPreviousReport();
		setLogger();
		ConfigLoader.config().setProperty("assert.screenshot", false);
	}

	@Override
	public void finish() {
		if (! finished.getAndSet(true)){
			generateReport();
		}

	}

	public void attach(byte[] attachment,String title, String mimeType){
		Allure.LIFECYCLE.fire(new MakeAttachmentEvent(attachment, title, mimeType));
	}

	public String name(){
		return "Allure";
	}

	
	public int order() {
		return 1000;
	}

	public static String scenarioName() {
		return currentScenario.get();
	}



}
