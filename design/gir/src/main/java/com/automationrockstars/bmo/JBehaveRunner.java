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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.data.CsvToTable;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.gson.JsonSyntaxException;

public class JBehaveRunner {


	private static final Logger LOG = LoggerFactory.getLogger(JBehaveRunner.class);
	public static final String[] DEFAULT_STORY_EXTENTIONS = {"story","feature","script"};
	private static final List<String> features = Lists.newCopyOnWriteArrayList();
	public static List<String> getFeatures() {

		if (features.isEmpty()){
			try {
				final File featureDir = new File(Resources.getResource("features").toURI());

				String[] storyExtenstions = ConfigLoader.config().getStringArray("bdd.story.files");
				storyExtenstions = (storyExtenstions == null || storyExtenstions.length == 0)?DEFAULT_STORY_EXTENTIONS : storyExtenstions;
				final String[] filter = ConfigLoader.config().getStringArray("bdd.story.filter");
				LOG.info("Using filters {}",Arrays.toString(filter));
				List<String> result = Lists.newArrayList(Iterables.filter(Iterables.transform(FileUtils.listFiles(featureDir, storyExtenstions, true),
						new Function<File, String>() {

					@Override
					public String apply(File input) {
						return "features/" + featureDir.toURI().relativize(input.toURI()).getPath();
					}
				}), new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						boolean hasAll = true;
						for (String partFilter : filter){
							hasAll = hasAll && input.contains(partFilter);
						}
						return hasAll;
					}
				}));
				features.addAll(result);
			} catch (URISyntaxException | IllegalArgumentException e) {
				throw new RuntimeException("Cannot find features to execute. Please make sure features folder contains files");
			}
		}
		LOG.info("Executing stories {}",features);
		Preconditions.checkArgument(! features.isEmpty(),"No story files has been found");
		addListenerStoryIfNeeded();
		return features;



	}

	private static boolean listenerActive = false;
	private static void addListenerStoryIfNeeded(){
		if (! listenerActive){
			ConfigLoader.addEventListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					if (event.getPropertyName().equals("bdd.story.files") || event.getPropertyName().equals("bdd.story.filter")){
						features.clear();
					}				
				}
			});
			listenerActive = true;
		}
	}
	private static void transformDataFile(File dataFile) throws IOException{
		Path origFile = Paths.get(dataFile.toURI());
		List<List<String>> data = CsvToTable.readCsv(origFile);
		Files.deleteIfExists(Paths.get(dataFile.getAbsolutePath()+".orig"));
		File movedFile =  new File(dataFile.getAbsolutePath()+".orig");
		FileUtils.moveFile(dataFile,movedFile);
		FileUtils.waitFor(movedFile, 2);
		if (! transformedFiles.contains(dataFile.getAbsolutePath()+".orig")){
			transformedFiles.add(dataFile.getAbsolutePath()+".orig");
		}
		if (dataFile.getName().contains(CsvToTable.VERTICAL)){
			data = CsvToTable.transform(data);
		}
		CsvToTable.writeAsTable(data, origFile);
	}
	public static void transformData(){
		final String dataDirectory = ConfigLoader.config().getString("bdd.data.files","data");
		try {
			File[] dataFiles = new File(Resources.getResource(dataDirectory).toURI()).listFiles();
			for (File dataFile : dataFiles){
				if (! dataFile.getName().endsWith(".orig"))
					transformDataFile(dataFile);
			}
		} catch (URISyntaxException | IOException | IllegalArgumentException e) {
			LOG.debug("Data transforming issue. Probably the "+dataDirectory+" directory is missing");
			LOG.trace("Exeption",e);
		}
	}

	public static final List<String> transformedFiles = Lists.newCopyOnWriteArrayList();
	public static void deleteTransformedFiles(){
		for (String filePath : transformedFiles){
			try {
				Path toDelete = Paths.get(filePath.replace(".orig", ""));
				Files.move(Paths.get(filePath), toDelete, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				LOG.error("File cannot be deleted {}",filePath,e);
			}
		}

	}

	private static BmoEmbedder embedder = new BmoEmbedder();
	public static BmoEmbedder embedder(){
		return embedder;
	}
	public static void executeFeatures() {
		try {
			transformData();
			embedder().runStoriesAsPaths(getFeatures());
		} catch (Throwable e){
			Throwables.propagate((e.getCause()!=null)?e.getCause():e);
		} finally {
			deleteTransformedFiles();
			LOG.info("Execution of stories finished");
			AllureStoryReporter.generateReport();

		}

	}

	public static Map<String,String> currentExample(){
		return AllureStoryReporter.currentExample.get();
	}
}
