package com.automationrockstars.design.desktop.driver;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;

public class SikulixProviderTest {

	@Test
	public void test() throws Exception {
//		GridLauncher.main(new String[]{"-role","hub"});
//		GridLauncher.main(new String[]{"-role","node","http://localhost:4444/grid/register","-browser","-browser"});
//		
//		
		ConfigLoader.config().setProperty("grid.url", "http://10.68.95.49:5555/wd/hub");
		ConfigLoader.config().setProperty("webdriver.browser", SikulixProvider.DRIVER_NAME);
		WebDriver driver = DriverFactory.getDriver();
//		SikuliDriver driver = SikuliDriver.driver();
		System.out.println(driver.findElement(new ByImage("fullJson.png")).getLocation());
		
		WebElement dd = driver.findElement(new ByImage("injson/dd.png"));
		Mouse m = ((HasInputDevices)driver).getMouse();
		m.mouseMove(((Locatable)dd).getCoordinates(),200,30);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,40);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,70);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,40);
		m.mouseMove(((Locatable)dd).getCoordinates(),200,90);
		DriverFactory.actions().moveToElement(dd).
		click().perform();
			dd.click();
		
		driver.manage().deleteAllCookies();
		
	}

}
