package com.automationrockstars.design.desktop.driver.internal;

import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Region;

import com.automationrockstars.design.desktop.driver.ByImage;
import com.automationrockstars.design.desktop.driver.ImageUiObject;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class SikuliImageUiObject extends UiObject implements ImageUiObject {

	private final String imagePath;

	private Match wrapped;
	private String name = null;
	private double timeout = 5;
	
	public SikuliImageUiObject(Match image, String imagePath) {
		this.imagePath = imagePath;
		this.wrapped = image;
		
	}
	public Match getImageElement(){
		return wrapped;
	}
	public WebElement getWrappedElement(){
		return this;	
	}

	public void click() {
		getImageElement().click();
	}

	public void sendKeys(CharSequence... keys){
		click();
		getImageElement().type(Joiner.on("").join(keys));
	}

	public void waitUntilVisible(){
		getImageElement();
	}

	public void waitUntilHidden(){
		SikuliDriver.waitUntilInvisible(imagePath, timeout);
	}

	@Override
	public String getText() {
		return getImageElement().text();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public By getLocator() {
		return new ByImage(imagePath);
	}

	@Override
	public Point getLocation() {
		return new Point(getImageElement().x, getImageElement().y);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(getImageElement().getW(), getImageElement().getH());
	}

	public String toString(){
		if (Strings.isNullOrEmpty(name)){
			return String.format("Element identified by image %s", imagePath.toString());
		} else {
			return getName();
		}
	}

	@Override
	public boolean isVisible() {
		Match element = SikuliDriver.isVisible(imagePath);
		if (element == null){
			return false;
		}
		this.wrapped = element;
		return true;
	}

	@Override
	public Keyboard getKeyboard() {
		return new SikuliKeyboard(getImageElement());
	}

	@Override
	public Mouse getMouse() {
		return new SikuliMouse(getImageElement());
	}

//	@Override
//	public Coordinates getCoordinates() {
//		return new Coordinates() {
//
//			@Override
//			public Point onScreen() {
//				Point location = getLocation();
//				return location;
//			}
//
//			@Override
//			public Point onPage() {
//				return onScreen();
//			}
//
//			@Override
//			public Point inViewPort() {
//				return onScreen();
//			}
//
//			@Override
//			public Object getAuxiliary() {
//				return getImageElement().toStringShort();
//			}
//		};
//	}

	@Override
	public void submit() {
		click();
		
		
	}

	@Override
	public void clear() {
		getImageElement().type(org.sikuli.script.Key.CTRL + "a");
		getImageElement().type(org.sikuli.script.Key.DELETE);
		
	}

	@Override
	public String getTagName() {
		return "desktopImage";
	}

	@Override
	public String getAttribute(String name) {
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public boolean isSelected() {
		return true;
//		throw new IllegalAccessError("Not supported");
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public List<WebElement> findElements(By by) {
		if (by instanceof ById){
			by = new ByImage(((ById)by).toString().split(" ")[1]);
		}
		return Lists.newArrayList(Iterators.transform(wrap(findAllWrapper(getImageElement(), ((ByImage)by).path(), null),((ByImage)by).path(),null),new Function<ImageUiObject, WebElement>() {
			@Override
			public WebElement apply(ImageUiObject input) {
				return input;
			}
		}));
	}

	@Override
	public WebElement findElement(By by) {
		String path = null;
		if (by instanceof ByImage){
			path = ((ByImage)by).path();
		} else {
			path = ByImage.translateFrom(by).path();
		}
		return wrap(findWrapper(getImageElement(), path, null),path,null);
	}

	@Override
	public boolean isDisplayed() {
		return isVisible();
	}

	@Override
	public Rectangle getRect() {
		return new Rectangle(getLocation(), getSize());
	}

	@Override
	public String getCssValue(String propertyName) {
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageUiObject findElement(ByImage by) {
		return findElement(by.path());
	}

	@Override
	public ImageUiObject findElement(String imagePath) {
		Preconditions.checkArgument(Paths.get(imagePath).toFile().canRead(),"Cannot read file %s",imagePath);
		return wrap(findWrapper(getImageElement(), imagePath, null),imagePath,null);
	}

	@Override
	public Iterator<ImageUiObject> findElements(ByImage by) {
		return findElements(by.path());
	}

	@Override
	public Iterator<ImageUiObject> findElements(String imagePath) {
		Preconditions.checkArgument(Paths.get(imagePath).toFile().canRead(),"Cannot read file %s",imagePath);
		return wrap(findAllWrapper(getImageElement(), imagePath, null),imagePath,null);
	}

	public void highlight(){
		getImageElement().highlight(1);
	}

	public static ImageUiObject wrap(Match toWrap, String imagePath,@Nullable String name){
		SikuliImageUiObject result = new SikuliImageUiObject(toWrap,imagePath);
		result.setName(name);
		return result;
		
	}
	
	public static Iterator<ImageUiObject> wrap(Iterator<Match> toWrap, final String imagePath,@Nullable final String name){
		return Iterators.transform(toWrap, new Function<Match, ImageUiObject>() {

			@Override
			public ImageUiObject apply(Match input) {
				return wrap(input, imagePath, name);
			}
		});
	}
	static Iterator<Match> findAllWrapper(Region where, String locator, @Nullable String name){
		try {
			return where.findAll(ImageCache.path(locator));
		} catch (FindFailed e) {
			throw new NoSuchElementException(String.format("Element %s not found on the screen", (name==null)?locator:name));
		}
	}
	
	static Match findWrapper(Region where, String locator,@Nullable String name){
		try {
			return where.wait(ImageCache.path(locator),0);
		} catch (FindFailed e) {
			throw new NoSuchElementException(String.format("Element %s not found on the screen", (name==null)?locator:name));
		}
	}
}
