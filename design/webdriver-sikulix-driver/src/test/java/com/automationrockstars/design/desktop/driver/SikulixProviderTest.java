package com.automationrockstars.design.desktop.driver;

import java.net.URL;

import org.junit.Test;
import org.openqa.grid.selenium.GridLauncher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class SikulixProviderTest {

	@Test
	public void test() throws Exception {
		GridLauncher.main(new String[]{"-role","hub"});
		GridLauncher.main(new String[]{"-role","node"});
//		
//		
//		ConfigLoader.config().setProperty("grid.url", "http://ws068261:5555/wd/hub");
		ConfigLoader.config().setProperty("webdriver.browser", SikulixProvider.DRIVER_NAME);
		WebDriver driver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"),SikulixProvider.driverCapabilities());
//		SikuliDriver driver = SikuliDriver.driver();
		try {
			WebElement ll =driver.findElement(new ByImage("fullJson.png")); 
		System.out.println(ll.getLocation());
		
		WebElement dd = ll.findElement(new ByImage("injson/dd.png"));
		Mouse m = ((HasInputDevices)driver).getMouse();
		m.mouseMove(((Locatable)dd).getCoordinates(),200,30);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,40);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,70);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,40);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,90);
		DriverFactory.actions().moveToElement(dd).
		click().perform();
			dd.click();
		} finally {
		driver.manage().deleteAllCookies();
		driver.close();
		driver.quit();
		}
	}

}
