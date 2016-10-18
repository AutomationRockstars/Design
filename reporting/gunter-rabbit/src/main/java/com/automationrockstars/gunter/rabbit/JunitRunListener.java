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
package com.automationrockstars.gunter.rabbit;

import static com.automationrockstars.gunter.events.EventFactory.createExecutionFinish;
import static com.automationrockstars.gunter.events.EventFactory.createExecutionStart;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.TestExecutionFinish;
import com.automationrockstars.gunter.events.TestExecutionStart;
import com.automationrockstars.gunter.events.EventBus;


public class JunitRunListener extends RunListener {
	private static final ThreadLocal<Map<String,Event>> context = new InheritableThreadLocal<>();
	private static final String EXEC_START = "executionStart";
	private static final String SUITE_START = "suiteStart";
	private static final String TC_START = "tcStart";
	private static final String PARENT = "parent";
	/**
    * Called before any tests have been run. This may be called on an
    * arbitrary thread.
    *
    * @param description describes the tests to be run
    */
   public void testRunStarted(Description description) throws Exception {
	   TestExecutionStart execStart = createExecutionStart(description.getDisplayName());
	   context.get().put(EXEC_START, execStart);
	   context.get().put(PARENT, execStart);
	   EventBus.fireEvent(EventFactory.toJson(execStart));
   }

   /**
    * Called when all tests have finished. This may be called on an
    * arbitrary thread.
    *
    * @param result the summary of the test run, including all the tests that failed
    */
   public void testRunFinished(Result result) throws Exception {
	   TestExecutionFinish execFinish = createExecutionFinish((TestExecutionStart)context.get().get(EXEC_START), String.valueOf(result.wasSuccessful()));
	   context.get().remove(EXEC_START);
	   context.get().remove(PARENT);
	   EventBus.fireEvent(EventFactory.toJson(execFinish));
   }

   /**
    * Called when an atomic test is about to be started.
    *
    * @param description the description of the test that is about to be run
    * (generally a class and method name)
    */
   public void testStarted(Description description) throws Exception {
	   
   }

   /**
    * Called when an atomic test has finished, whether the test succeeds or fails.
    *
    * @param description the description of the test that just ran
    */
   public void testFinished(Description description) throws Exception {
   }

   /**
    * Called when an atomic test fails, or when a listener throws an exception.
    *
    * <p>In the case of a failure of an atomic test, this method will be called
    * with the same {@code Description} passed to
    * {@link #testStarted(Description)}, from the same thread that called
    * {@link #testStarted(Description)}.
    *
    * <p>In the case of a listener throwing an exception, this will be called with
    * a {@code Description} of {@link Description#TEST_MECHANISM}, and may be called
    * on an arbitrary thread.
    *
    * @param failure describes the test that failed and the exception that was thrown
    */
   public void testFailure(Failure failure) throws Exception {
   }

   /**
    * Called when an atomic test flags that it assumes a condition that is
    * false
    *
    * @param failure describes the test that failed and the
    * {@link org.junit.AssumptionViolatedException} that was thrown
    */
   public void testAssumptionFailure(Failure failure) {
   }

   /**
    * Called when a test will not be run, generally because a test method is annotated
    * with {@link org.junit.Ignore}.
    *
    * @param description describes the test that will not be run
    */
   public void testIgnored(Description description) throws Exception {
   }


   /**
    * Indicates a {@code RunListener} that can have its methods called
    * concurrently. This implies that the class is thread-safe (i.e. no set of
    * listener calls can put the listener into an invalid state, even if those
    * listener calls are being made by multiple threads without
    * synchronization).
    *
    * @since 4.12
    */
   @Documented
   @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface ThreadSafe {
   }
}
