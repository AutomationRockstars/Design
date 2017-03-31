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
package com.automationrockstars.base;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.internal.RuntimeSetterDecorator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath.ResourceInfo;

import eu.infomas.annotation.AnnotationDetector;
import eu.infomas.annotation.AnnotationDetector.Reporter;

public class ConfigLoader {

	private static CompositeConfiguration config;
	public static final String PROPS = "test_props";
	public static Configuration config(){
		return getConfig();
	}
	private static Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

	private static CompositeConfiguration getConfig(){
		if (config == null){
			init();
		}		
		return config;
	}

	private static synchronized void init(){
		PropertiesConfiguration props = new PropertiesConfiguration();
		try {
			FluentIterable<ResourceInfo> propFiles = JarUtils.findResources(PROPS + "/.*properties$");
			LOG.info("Found properties resources {}",propFiles);
			for (ResourceInfo propFile : propFiles){

				LOG.info("Opening {}",propFile);
				try (InputStream propContent = propFile.url().openStream()){
					props.load(propContent);
				} catch (Throwable e) {
					LOG.error("Error loading resource: {}",e.getMessage());
				}
			}			
			populate(Paths.get(PROPS).toFile(), props);
		} catch (IllegalArgumentException  e) {
			LOG.error("Error loading file configuration: {}",e.getMessage());
		}

		config = new RuntimeSetterDecorator(
				Arrays.asList(
						new SystemConfiguration(),
						new EnvironmentConfiguration(),
						props
						));
		for (int i =0;i<config.getNumberOfConfigurations();i++){
			LOG.debug(logConfig(config.getConfiguration(i)));

		}
	}

	private static void populate(File dir, PropertiesConfiguration props){
		if (dir.exists() && dir.isDirectory()){
			for (File propFile : dir.listFiles()){
				LOG.info("Loading properties file {}",propFile);
				try {
					props.load(propFile);
					LOG.info("Properties loaded from file {}", propFile);
					LOG.debug(logConfig(props));
				} catch (ConfigurationException e) {
					LOG.error("Error loading file {} {}",propFile, e.getMessage());		
				}
			}
		}
	}

	public static String logConfig(Configuration c){
		StringBuilder result = new StringBuilder();
		for (String key : Iterators.toArray(c.getKeys(),String.class)){
			try {
				result.append(String.format("%s - %s : %s",c.toString().replaceAll(".*commons.configuration.", "").replaceAll("@.*", ""),key,c.getString(key)))
				.append("\n");
			} catch (Throwable ignore){}
		}
		return result.toString();
	}

	public static void addEventListener(ConfigurationListener listener){
		((EventSource)getConfig()).addConfigurationListener(listener);
	}

	private static final ConcurrentMap<Reporter, AnnotationDetector> detectors = Maps.newConcurrentMap();
	public static AnnotationDetector annotationDetector(Reporter reporter){
		return detectors.putIfAbsent(reporter, new AnnotationDetector(reporter)) ;
	}
	
	public static void addConfiguration(Configuration config){
		getConfig().addConfiguration(config);
		LOG.debug("Configuration loaded {}",config);
	}
	
	public static void addToConfiguration(Path propertiesFile){
		
		try {
			Configuration prop = new PropertiesConfiguration(propertiesFile.toFile());
			addConfiguration(prop);
		} catch (ConfigurationException e) {
			LOG.error("File {} cannot be loaded to configuration",propertiesFile,e);
		}
	}
}
