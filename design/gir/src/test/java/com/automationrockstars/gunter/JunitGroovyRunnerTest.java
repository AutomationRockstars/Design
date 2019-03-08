/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */
package com.automationrockstars.gunter;

import com.automationrockstars.bmo.JunitGroovyRunner;
import com.google.common.io.Files;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JunitGroovyRunnerTest {

    @Test
    public void should_RunScript() throws Exception {
        Path scriptFile = Paths.get(Files.createTempDir().getAbsolutePath(), "file.groovy");
        String scriptContent = "testScript: \"BOLEK\" \nstep title: \"stefab\" \nverify: {println \"sdsd\"} ";

        Files.write(scriptContent.getBytes(), scriptFile.toFile());
        System.setProperty(JunitGroovyRunner.SCRIPT_PROP, scriptFile.toString());
        new JunitGroovyRunner().executeScript();

    }

}
