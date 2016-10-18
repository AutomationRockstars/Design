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
package com.automationrockstars.gunter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.automationrockstars.bmo.JunitGroovyRunner;
import com.google.common.io.Files;

public class JunitGroovyRunnerTest {

	@Test
	public void should_RunScript() throws Exception {
		Path scriptFile = Paths.get(Files.createTempDir().getAbsolutePath(),"file.groovy");
		String scriptContent = "testScript: \"BOLEK\" \nstep title: \"stefab\" \nverify: {println \"sdsd\"} ";
		
		Files.write(scriptContent.getBytes(), scriptFile.toFile());
		System.setProperty(JunitGroovyRunner.SCRIPT_PROP,scriptFile.toString());
		new JunitGroovyRunner().executeScript();
		
	}

}
