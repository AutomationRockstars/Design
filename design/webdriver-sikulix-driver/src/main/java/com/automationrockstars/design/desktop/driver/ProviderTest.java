package com.automationrockstars.design.desktop.driver;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.gir.desktop.ByImage;

public class ProviderTest {

	@Test
	public void test() throws Exception {
//		GridLauncher.main(new String[]{"-role","hub"});
//		GridLauncher.main(new String[]{"-role","node","http://localhost:4444/grid/register","-browser","browserName=sikulix,version=1.0.4,platform=WINDOWS"});
//		
//		
//		ConfigLoader.config().setProperty("grid.url", "http://localhost:4444/wd/hub");
		ConfigLoader.config().setProperty("webdriver.browser", SikulixProvider.DRIVER_NAME);
		WebDriver driver = DriverFactory.getDriver();
//		SikuliDriver driver = SikuliDriver.driver();
		System.out.println(driver.findElement(new ByImage("fullJson.png")).getLocation());
		
		WebElement dd = driver.findElement(new ByImage("injson/dd.png"));
		DriverFactory.actions().moveToElement(dd).
		click().perform();
			
		
		driver.manage().deleteAllCookies();
		
	}

}
