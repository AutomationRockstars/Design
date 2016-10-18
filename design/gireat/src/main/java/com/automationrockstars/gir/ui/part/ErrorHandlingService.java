package com.automationrockstars.gir.ui.part;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class ErrorHandlingService {

	public static final String SHOW_SCREENSHOT = "webdriver.onerror.display";
	private static Logger LOG = LoggerFactory.getLogger(ErrorHandlingService.class);
	public static void handle(Throwable t, Object host, Method method, Object[] args) {
		LOG.error("Error on {} within {}",method.getName(),host,t);
		boolean display = ConfigLoader.config().getBoolean(SHOW_SCREENSHOT,true);
		if (display) {
			DriverFactory.displayScreenshotFile();
		} else {
			String path = DriverFactory.getScreenshotFile().getAbsolutePath();
			LOG.error("Screenshot saved to {}",path);
		}
	}

}
