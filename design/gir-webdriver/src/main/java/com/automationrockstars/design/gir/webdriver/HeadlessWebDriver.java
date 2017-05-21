package com.automationrockstars.design.gir.webdriver;

import org.apache.commons.lang3.SystemUtils;

public class HeadlessWebDriver {

	public static String name(){
		try {
			if (SystemUtils.IS_OS_WINDOWS){
				new ProcessBuilder("cmd","/C","phantomjs", "--version").inheritIO().start().waitFor();
			} else {
				new ProcessBuilder("bash","phantomjs","--version").inheritIO().start().waitFor();
			}
			System.setProperty("webdriver.phantomjs.driver", "phantomjs");
			return "phantomjs";
		} catch (Throwable t){
			t.printStackTrace();
			return "htmlUnit";
		}
	}
}
