package com.automationrockstars.design.gir.webdriver;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadlessWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(HeadlessWebDriver.class);
			
	public static String name(){
		String broser = "";
		try {
			LOG.info("Detectong phantoom version");
			if (SystemUtils.IS_OS_WINDOWS){
				new ProcessBuilder("cmd","/C","phantomjs", "--version").inheritIO().start().waitFor();
			} else {
				new ProcessBuilder("bash","phantomjs","--version").inheritIO().start().waitFor();
			}
			System.setProperty("webdriver.phantomjs.driver", "phantomjs");
			broser = "phantomjs";
		} catch (Throwable t){
			LOG.debug("Phantomjs detection failed due to {}",t);
			broser = "htmlUnit";
		}
		LOG.info("Found headless browser {}",broser);
		return broser;
	}
}
