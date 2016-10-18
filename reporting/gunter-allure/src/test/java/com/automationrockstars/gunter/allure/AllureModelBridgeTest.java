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
package com.automationrockstars.gunter.allure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.AllureReportBuilderException;

public class AllureModelBridgeTest {

	@Ignore
	@Test
	public void test() throws IOException, AllureReportBuilderException {
		AllureModelBridge mb = new AllureModelBridge();
		mb.startRun("a", System.currentTimeMillis());
		mb.addSuite("1", "suite1", System.currentTimeMillis());
		mb.addTest("1.1", "1", "test1", System.currentTimeMillis());
		mb.addStep("1.1.1", "1.1", "step1", System.currentTimeMillis());
		mb.finish("1.1.1", System.currentTimeMillis()+100);
		mb.finish("1.1", System.currentTimeMillis()+100);
		mb.finish("1", System.currentTimeMillis()+100);
		mb.finish("1", System.currentTimeMillis()+100);
		mb.finishRun(System.currentTimeMillis()+100);
		
		Path in = Paths.get("in");
		Path out = Paths.get("out");
		try {
		Files.createDirectory(in);
		Files.createDirectories(out);
		} catch (Exception e){}
		Files.write(Paths.get("in", UUID.randomUUID().toString()+"-testsuite.xml"), mb.toXml().get(0).getBytes(), StandardOpenOption.CREATE_NEW);
//		mb.generate(in, out);
		
		AllureReportBuilder bl = new AllureReportBuilder("1.4.17", out.toFile());
		bl.processResults(in.toFile());
		bl.unpackFace();
	}

}
