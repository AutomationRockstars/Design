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

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jbehave.core.failures.FailureStrategy;
import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAndRethrowFailure implements FailureStrategy{


	public static final CopyOnWriteArrayList<Throwable> errors = new CopyOnWriteArrayList<>();

	private static final Logger LOG = LoggerFactory.getLogger(LogAndRethrowFailure.class);
	@Override
	public void handleFailure(Throwable throwable) throws Throwable {

		if ( throwable instanceof UUIDExceptionWrapper ){
			throwable =  ((UUIDExceptionWrapper)throwable).getCause();
		}
		for (Method m : discoverFailureHandlers(BmoEmbedder.packageName)){
			try {
				m.invoke(m.getDeclaringClass().newInstance(), throwable);
			} catch (Throwable ignore){
				//Doing best effort but needs to call all
			}
		}

		errors.add(throwable);
		LOG.error("Error during step execution.",throwable);
		throw throwable;
	}


	private Set<Method> discoverFailureHandlers(String packageName){
		Reflections search = new Reflections(packageName);
		return search.getMethodsAnnotatedWith(FailureHandler.class);
	}

}
