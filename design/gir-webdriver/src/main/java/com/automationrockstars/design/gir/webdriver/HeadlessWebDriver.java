package com.automationrockstars.design.gir.webdriver;

import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.UserAgent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.settings.io.SettingsParseException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import java.util.logging.Level;

public class HeadlessWebDriver {

	private static final Logger LOG = LoggerFactory.getLogger(HeadlessWebDriver.class);

	public static String name() {
		String broser = "";
		try {
			WebDriver dr = (WebDriver) Class.forName("com.machinepublishers.jbrowserdriver.JBrowserDriver").newInstance();
			return "com.machinepublishers.jbrowserdriver.JBrowserDriver";
		} catch (Throwable t){
            t.printStackTrace();
		}

		try {
			LOG.info("Detecting phantomjs version");

			String[] findPhantom;
			if (SystemUtils.IS_OS_WINDOWS) {
				findPhantom = new String[] { "cmd", "/C", "where", "phantomjs" };
			} else {
				findPhantom = new String[] { "which", "phantomjs" };
			}

			Process pr = new ProcessBuilder(findPhantom).redirectErrorStream(true).start();
			String output = IOUtils.toString(pr.getInputStream());
			Preconditions.checkState(StringUtils.contains(output, "phantomjs"),"Location of phantomjs not found %s",output);
			String location = Splitter.on("\n").splitToList(output).get(0);
			location = location.replaceAll("\r", "");
			LOG.info("Using phantomjs location {}", location);
			System.setProperty("webdriver.phantomjs.driver", location);
			int result = new ProcessBuilder(location,"--version").inheritIO().start().waitFor();
			LOG.info("Phantom version test {}",result);
			broser = "phantomjs";
		} catch (Throwable t) {
			LOG.debug("Phantomjs detection failed due to {}", t);
			broser = "htmlUnit";
		}
		LOG.info("Found headless browser {}", broser);
		return broser;
	}

    public static Capabilities jDriverCapabilities() {
        return Settings.builder()
                .userAgent(UserAgent.CHROME)
                .loggerLevel(Level.ALL)
                .headless(false).buildCapabilities();
    }
}
