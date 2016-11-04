package com.automationrockstars.gir.ui.part;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.io.Files;

public class ErrorHandlingService {

	public static final String SHOW_SCREENSHOT = "webdriver.onerror.display";
	private static Logger LOG = LoggerFactory.getLogger(ErrorHandlingService.class);
	public static void handle(Throwable t, Object host, Method method, Object[] args) {
		LOG.error("Error on {} with {}",host, method.getName(),t);
		String fileName = String.format("%s_%s_%s",method.getName(),t.getMessage(),System.currentTimeMillis()).replaceAll("[^a-zA-Z0-9.-]", "_");
		try {
			Files.write(DriverFactory.getDriver().getPageSource().getBytes(), Paths.get(fileName+".html").toFile());
		} catch (IOException ignore) {
		}
		boolean display = ConfigLoader.config().getBoolean(SHOW_SCREENSHOT,true);
		if (display) {
			DriverFactory.displayScreenshotFile();
		} else {
			String path = DriverFactory.getScreenshotFile().getAbsolutePath();
			try {
				FileUtils.moveFile(new File(path), new File(fileName+".png"));
				LOG.error("Screenshot saved to {}",fileName + ".png");
			} catch (IOException e) {
				LOG.error("Screenshot saved to {}",path);
			}
			
		}
		
		
		
	}

}
