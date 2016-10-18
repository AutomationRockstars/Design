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
package org.openqa.selenium.phantomjs;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriverException;

import org.openqa.selenium.remote.service.DriverService;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class PhantomjsDriverService extends DriverService {

	/**
	 * System property that defines the location of the MicrosoftWebDriver executable that will be used by
	 * the {@link #createDefaultService() default service}.
	 */
	public static final String PHANTOMJS_DRIVER_EXE_PROPERTY = "webdriver.phantomjs.driver";

	public PhantomjsDriverService(File executable, int port, ImmutableList<String> args,
			ImmutableMap<String, String> environment) throws IOException {
		super(executable, port, args, environment);
	}

	/**
	 * Configures and returns a new {@link PhantomjsDriverService} using the default configuration. In
	 * this configuration, the service will use the MicrosoftWebDriver executable identified by the
	 * {@link #PHANTOMJS_DRIVER_EXE_PROPERTY} system property. Each service created by this method will
	 * be configured to use a free port on the current system.
	 *
	 * @return A new PhantomjsDriverService using the default configuration.
	 */
	public static PhantomjsDriverService createDefaultService() {
		return new Builder().usingAnyFreePort().build();
	}

	public static class Builder extends DriverService.Builder<
	PhantomjsDriverService, PhantomjsDriverService.Builder> {

		@Override
		protected File findDefaultExecutable() {
			return findExecutable("PhantomjsDriver", PHANTOMJS_DRIVER_EXE_PROPERTY,
					"http://phantomjs.org/",
					"http://phantomjs.org/download.html");
		}

		@Override
		protected ImmutableList<String> createArgs() {
			ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
			String proxy= ConfigLoader.config().getString("webdriver.proxy"); 
			if (! Strings.isNullOrEmpty(proxy)){
				String[] proxyParts = proxy.split("@");
				if (proxyParts.length > 1){
					argsBuilder.add(String.format("--proxy-auth=%s", proxyParts[0]));
					argsBuilder.add(String.format("--proxy=%s", proxyParts[1]));
				} else {
					argsBuilder.add(String.format("--proxy=%s", proxyParts[0]));
				}			
			}
			argsBuilder.add(String.format("--ignore-ssl-errors=%s", ! ConfigLoader.config().getBoolean("verify.ssl",false)));
			argsBuilder.add(String.format("--webdriver=%d", getPort()));
			return argsBuilder.build();
		}

		protected PhantomjsDriverService createDriverService(File exe, int port,
				ImmutableList<String> args,
				ImmutableMap<String, String> environment) {
			try {
				return new PhantomjsDriverService(exe, port, args, environment);
			} catch (IOException e) {
				throw new WebDriverException(e);
			}
		}
	}



}
