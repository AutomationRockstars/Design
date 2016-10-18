/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
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
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.Test;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.bmo.TestScriptBase;
import com.automationrockstars.design.gir.Logic;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.MapEntry;


public class JunitGroovyRunner {
	public static final String LOGIC_PACKAGE_PROP = "logic.package";

	public static final String SCRIPT_PROP = "script";

	private final String logicPackage;
	public JunitGroovyRunner(){
		this(System.getProperty(LOGIC_PACKAGE_PROP));
	}
	public JunitGroovyRunner(String logicPackage){
		this.logicPackage = logicPackage;
	}
	protected synchronized void prepareBinding(){
		String packageName = (logicPackage == null)?"":logicPackage;

		for (Entry<String, Object> var : getLogic(packageName).entrySet()){
			binding.setVariable(var.getKey(), var.getValue());
		}
		log.info("Binding prepared {}", binding.getVariables());

	}

	private static synchronized Map<String,Object> getLogic(String packageName){
		Map<String,Object> result = Maps.newHashMap();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Logic.class);
		for (Class<?> logic : annotated){
			try {
				log.info("Found logic class {}",logic);
				String name = logic.getAnnotation(Logic.class).value();
				Object instance = null;
				if (name.equals("")){
					name = logic.getSimpleName(); 
				}
				
//				Set<Constructor> constructors = reflections.getConstructorsMatchParams(WebDriver.class);
//				if (constructors.isEmpty()){
					instance = logic.newInstance();
//				} else {
//					instance = constructors.iterator().next().newInstance(driver);
//				}
				if (instance != null){
					result.put(name, instance);
					log.info("Logic class {} injected as {}",logic,name);
				}
			} catch (Exception e) {
				log.error("Injecting logic for {} failed due to {}",logic,e);
			}

		}
		return result;
	}
	
	private static final Logger log = LoggerFactory.getLogger(JunitGroovyRunner.class);
	
	protected Binding binding = new Binding();
	
	public void executeScript() throws Exception{
		String testScript = System.getProperty(SCRIPT_PROP,System.getenv(SCRIPT_PROP));
		Preconditions.checkNotNull(testScript, "System property or environment variable %s is not set. Please set it using -D%s=scriptName", SCRIPT_PROP);
		CompilerConfiguration configuration = new CompilerConfiguration();
		configuration.setScriptBaseClass(TestScriptBase.class.getName());
		prepareBinding();
		GroovyShell groovyShell = new GroovyShell(binding,configuration);
		log.info("Start execution of script script {}",testScript);
		Object scriptResult = null;
		try {
			scriptResult =  groovyShell.evaluate(new File(testScript));
		} catch (Throwable e){
			log.error("Script execution failed",e);
			Throwables.propagate(e);
		} finally {
			log.info("Script {} result: {} ", testScript,Objects.toString(scriptResult));

		}
	}
}
