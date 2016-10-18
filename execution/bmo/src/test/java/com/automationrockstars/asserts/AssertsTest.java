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
package com.automationrockstars.asserts;

import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;
public class AssertsTest {

	@Test
	public void test() {
		try {
		assertThat("expected string is not presented","Some test".contains("assert"));/* fails with java.lang.AssertionError: expected string is not presented */
		} catch (Throwable e){
			e.printStackTrace();
		}
		
		try {
			assertThat("Some test", containsString("assert"));/* fails with 
			java.lang.AssertionError: 
			Expected: a string containing "assert"
			     but: was "Some test"
			*/
		}catch (Throwable e){
			e.printStackTrace();
		}
		
		try {
		assertThat("expected string is not presented", "Some test", containsString("assert"));	/*fails with java.lang.AssertionError: expected string is not presented
Expected: a string containing "assert"
     but: was "Some test"
		*/
		}catch (Throwable e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void softAssertsTest(){
		softAssertThat("expected string is not presented","Some test".contains("assert"));
		softAssertThat("Some test", containsString("assert"));
		softAssertThat("expected string is not presented", "Some test", containsString("assert"));
		try {
			checkForErrors();
		} catch (AssertionError e){
			e.printStackTrace();
		}
	}

}
