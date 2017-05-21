package com.automationrockstars.design.gir.webdriver;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

public class HeadlessWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(HeadlessWebDriver.class);
			
	public static String name(){
		String broser = "";
		try {
			LOG.info("Detectong phantoom version");
			String[] hasPhantom;
			String[] findPhantom;
			if (SystemUtils.IS_OS_WINDOWS){
				hasPhantom = new String[] {"cmd","/C","phantomjs","--version"};
				findPhantom = new String[] { "cmd","/C","where","phantomjs"};
			} else {
				hasPhantom = new String[] {"bash","-c", "\"phantomjs --version\""};
				findPhantom = new String[] {"bash",",-c", "\"which phantomjs\""};
				}
			
			
			if (new ProcessBuilder(hasPhantom).inheritIO().start().waitFor() == 0){
				Process pr = new ProcessBuilder(findPhantom).redirectErrorStream(true).start();
				String output = IOUtils.toString(pr.getInputStream());
				String location = Splitter.on("\n").splitToList(output).get(0);
				location= location.replaceAll("\r", "");
				LOG.info("Using phantomjs location {}",location);
				System.setProperty("webdriver.phantomjs.driver", location);
				
			}
			
			broser = "phantomjs";
		} catch (Throwable t){
			LOG.debug("Phantomjs detection failed due to {}",t);
			broser = "htmlUnit";
		}
		LOG.info("Found headless browser {}",broser);
		return broser;
	}
}
