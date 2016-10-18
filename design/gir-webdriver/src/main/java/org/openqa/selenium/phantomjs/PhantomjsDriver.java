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

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverCommandExecutor;

public class PhantomjsDriver extends RemoteWebDriver {
	/**
	   * Creates a new PhantomjsDriver using the {@link PhantomjsDriverService#createDefaultService default}
	   * server configuration.
	   *
	   * @see #PhantomjsDriver(PhantomjsDriverService, PhantomjsOptions)
	   */
	  public PhantomjsDriver() {
		    this(PhantomjsDriverService.createDefaultService(), new PhantomjsOptions());
		  }

	  /**
	   * Creates a new PhantomjsDriver instance. The {@code service} will be started along with the driver,
	   * and shutdown upon calling {@link #quit()}.
	   *
	   * @param service The service to use.
	   * @see #PhantomjsDriver(PhantomjsDriverService, PhantomjsOptions)
	   */
	  public PhantomjsDriver(PhantomjsDriverService service) {
	    this(service, new PhantomjsOptions());
	  }

	  /**
	   * Creates a new PhantomjsDriver instance. The {@code capabilities} will be passed to the
	   * Phantomjsdriver service.
	   *
	   * @param capabilities The capabilities required from the PhantomjsDriver.
	   * @see #PhantomjsDriver(PhantomjsDriverService, Capabilities)
	   */
	  public PhantomjsDriver(Capabilities capabilities) {
	    this(PhantomjsDriverService.createDefaultService(), capabilities);
	  }

	  /**
	   * Creates a new PhantomjsDriver instance with the specified options.
	   *
	   * @param options The options to use.
	   * @see #PhantomjsDriver(PhantomjsDriverService, PhantomjsOptions)
	   */
	  public PhantomjsDriver(PhantomjsOptions options) {
	    this(PhantomjsDriverService.createDefaultService(), options);
	  }

	  /**
	   * Creates a new PhantomjsDriver instance with the specified options. The {@code service} will be
	   * started along with the driver, and shutdown upon calling {@link #quit()}.
	   *
	   * @param service The service to use.
	   * @param options The options to use.
	   */
	  public PhantomjsDriver(PhantomjsDriverService service, PhantomjsOptions options) {
	    this(service, options.toCapabilities());
	  }
	  
	  /**
	   * Creates a new PhantomjsDriver instance. The {@code service} will be started along with the
	   * driver, and shutdown upon calling {@link #quit()}.
	   *
	   * @param service The service to use.
	   * @param capabilities The capabilities required from the PhantomjsDriver.
	   */
	  public PhantomjsDriver(PhantomjsDriverService service, Capabilities capabilities) {
	    super(new DriverCommandExecutor(service), capabilities);
	  }

}
