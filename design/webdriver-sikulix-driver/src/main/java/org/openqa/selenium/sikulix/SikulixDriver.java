package org.openqa.selenium.sikulix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.sikuli.script.Screen;

import com.automationrockstars.design.desktop.driver.ByImage;
import com.automationrockstars.design.desktop.driver.internal.ImageCache;
import com.automationrockstars.design.desktop.driver.internal.SikuliDriver;
import com.automationrockstars.design.desktop.driver.internal.SikuliKeyboard;
import com.automationrockstars.design.desktop.driver.internal.SikuliMouse;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class SikulixDriver  implements WebDriver, TakesScreenshot, HasCapabilities,HasInputDevices, FindsById{

	private final SikuliDriver driver;
	
	private static final DesiredCapabilities base = new DesiredCapabilities("sikulix", "1.0.4", Platform.WINDOWS);
	private final DesiredCapabilities caps;
	
	public SikulixDriver(Capabilities capabilities){
		this();
		caps.merge(capabilities);
	}
	public SikulixDriver() {
		caps = new DesiredCapabilities(capabilities());
		this.driver = SikuliDriver.driver();
	}
	

	@Override
	public List<WebElement> findElements(By by) {
		return translate(by).findElements(driver);
	}

	@Override
	public WebElement findElement(By by) {
		return translate(by).findElement(driver);
	}

	@Override
	public void close() {
	}

	@Override
	public void quit() {
	}

	@Override
	public Set<String> getWindowHandles() {
		return Sets.newHashSet(getWindowHandle());
	}

	@Override
	public String getWindowHandle() {
		return "Desktop";
	}

	private static ByImage translate(By by){
		if (by instanceof ByImage){
			return (ByImage) by;
		}
		Preconditions.checkArgument(by instanceof ById ,"Cannot translate locator");
		String imagePath = by.toString().replaceAll("By.id: ", "");
		return new ByImage(imagePath);
	}


	@Override
	public Keyboard getKeyboard() {
		return new SikuliKeyboard(Screen.all());
	}


	@Override
	public Mouse getMouse() {
		return new SikuliMouse(Screen.all());
	}

	public static final Capabilities capabilities(){
		base.setCapability("cannot_wrap", true);
		return base;
	}
	
	@Override
	public Capabilities getCapabilities() {
		return capabilities();
	}


	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			
			ImageIO.write(Screen.as(0).capture().getImage(), "png", baos);
			byte[] bytes = baos.toByteArray();
			baos.flush();
			return target.convertFromPngBytes(bytes);
		} catch (IOException e) {
			throw new WebDriverException(e);
		}
		
	}


	@Override
	public WebElement findElementById(String using) {
		return new ByImage(using).findElement(driver);
	}


	@Override
	public List<WebElement> findElementsById(String using) {
		return new ByImage(using).findElements(driver);
	}
	
	
	@Override
	public Options manage() {
		return new Options() {
			
			@Override
			public Window window() {
				return null;
			}
			
			@Override
			public Timeouts timeouts() {
				return null;
			}
			
			@Override
			public Logs logs() {
				return null;
			}
			
			@Override
			public ImeHandler ime() {
				return null;
			}
			
			@Override
			public Set<Cookie> getCookies() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Cookie getCookieNamed(String name) {
				return null;
			}
			
			@Override
			public void deleteCookieNamed(String name) {
				ImageCache.remove(name);
				
			}
			
			@Override
			public void deleteCookie(Cookie cookie) {
				ImageCache.remove(cookie.getName());
				
			}
			
			@Override
			public void deleteAllCookies() {
				ImageCache.removeAll();
				
			}
			
			@Override
			public void addCookie(Cookie cookie) {
				String imageId = cookie.getName();
				String imageContent = cookie.getValue();
				ImageCache.add(imageId, imageContent);
				
			}
		};
	}


	@Override
	public void get(String url) {			
	}


	@Override
	public String getCurrentUrl() {
		return getTitle();
	}


	@Override
	public String getTitle() {
		return "Screen";
	}


	@Override
	public String getPageSource() {
		return "";
	}


	@Override
	public TargetLocator switchTo() {
		throw new UnsupportedOperationException("Navigation on desctop is not supproted");
	}


	@Override
	public Navigation navigate() {
		throw new UnsupportedOperationException("Navigation on desctop is not supproted");
	}
}

