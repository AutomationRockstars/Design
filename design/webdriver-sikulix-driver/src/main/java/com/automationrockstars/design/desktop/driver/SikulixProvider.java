package com.automationrockstars.design.desktop.driver;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.server.DriverProvider;
import org.openqa.selenium.sikulix.SikulixDriver;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.GridUtils;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;


public class SikulixProvider implements DriverProvider{

	public static final String DRIVER_NAME = "sikulix";
	
	
	public static Capabilities driverCapabilities(){
		return new DesiredCapabilities(DRIVER_NAME, "", Platform.WINDOWS);
	}
	@Override
	public Capabilities getProvidedCapabilities() {
		return driverCapabilities();
	}

	@Override
	public boolean canCreateDriverInstances() {
		return true;
	}

	@Override
	public boolean canCreateDriverInstanceFor(Capabilities capabilities) {
		return capabilities.getBrowserName().equals(DRIVER_NAME) && (capabilities.getPlatform().is(Platform.WINDOWS) || capabilities.getPlatform().is(Platform.ANY)) ;
	}

	@Override
	public WebDriver newInstance(Capabilities capabilities) {
		return new SikulixDriver();
	}
	
	public static WebDriver supporting(WebDriver toSupport){
		String gridUrl = ConfigLoader.config().getString("grid.url");
		if (toSupport instanceof RemoteWebDriver && ! Strings.isNullOrEmpty(gridUrl)){
			String node = GridUtils.getNode(gridUrl, toSupport);
			try {
				return new RemoteWebDriver(new URL(node),driverCapabilities());
			} catch (MalformedURLException e) {
				Throwables.propagate(e);
				return null;
			}
		} else return new SikulixDriver();
	}

}
