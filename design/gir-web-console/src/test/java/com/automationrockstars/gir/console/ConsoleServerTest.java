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
package com.automationrockstars.gir.console;

import com.automationrockstars.base.ConfigLoader;
import org.junit.Test;

public class ConsoleServerTest {

    @Test
    public void test() throws Exception {
        if (!ConfigLoader.config().containsKey("noui")) {
            ConsoleServer.startServer();
//			DesiredCapabilities d = DesiredCapabilities.chrome();
//			d.setCapability("debugSessionId", "stefan");
//			ConfigLoader.config().addProperty("webdriver.browser", "chrome");
//			WebDriver dr = DriverFactory.getDriver(d);
//			dr.get("http://d7djvv22w7:8181/time");


//				Thread.sleep(6000000);
//				DriverFactory.closeDriver();
            ConsoleServer.stopServer();

        }
    }

}
