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
package com.automationrockstars.gir.cli;

import static net.sf.expectit.filter.Filters.removeColors;
import static net.sf.expectit.filter.Filters.removeNonPrintable;
import static net.sf.expectit.matcher.Matchers.contains;
import static net.sf.expectit.matcher.Matchers.regexp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;

import net.schmizz.sshj.common.IOUtils;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;


public class ScriptHelper {

	
	
	public static long DEFAULT_COMMAND_TIMEOUT = 7200;

	private Terminal terminal;
	private Expect expect;

	public ScriptHelper(String hostname, String user, String password) {
		try {
			terminal = SshConnector.connect(hostname, user, password);
			expect = new ExpectBuilder()
	                .withOutput(terminal.shell.getOutputStream())
	                .withInputs(terminal.shell.getInputStream(), terminal.shell.getErrorStream())
	                .withInputFilters(removeColors(), removeNonPrintable())
	                .withExceptionOnFailure()
	                .build();
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		
	}

	public ScriptHelper(String hostname, String user, Path passFile){
		try {
			terminal = SshConnector.connect(hostname, user, passFile);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}


	public String exec(String command) {
		try {
			return terminal.exec(command, DEFAULT_COMMAND_TIMEOUT).getOutput();
		} catch (IOException e) {
			return null;
		}
	}

	
	
	public void disconnect() {
		IOUtils.closeQuietly(terminal);
	}

	
	public String expect(String expectedOutcome) throws IOException {
		return expect.expect(contains(expectedOutcome)).getBefore();
	}

	public String expect(String expectedOutcome, long timeoutInSeconds) throws IOException {
		return expect.withTimeout(timeoutInSeconds, TimeUnit.SECONDS).expect(contains(expectedOutcome)).getBefore();
	}

	public String expect(Pattern pattern) throws IOException {
		return expect.expect(regexp(pattern)).getBefore();
	}

	public String expect(Pattern pattern, long timeoutInSeconds) throws IOException {
		return expect.withTimeout(timeoutInSeconds, TimeUnit.SECONDS).expect(regexp(pattern)).getBefore();
	}

	public String expectErr(String err) throws IOException {
		return expect.expectIn(1, contains(err)).getBefore();
	}

	public String expectErr(String err, long timeoutInSeconds) throws IOException {
		return expect.withTimeout(timeoutInSeconds, TimeUnit.SECONDS).expectIn(1, contains(err)).getBefore();
	}

	public void write(String text) throws IOException {
		expect.send(text);
	}

	public String getStdOut() {
		return terminal.getStdOut();
	}
	
	public String getStdErr(){
		return terminal.getStdErr();
	}

}
