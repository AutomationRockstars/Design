package com.automationrockstars.gir.desktop.internal;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.openqa.selenium.By;
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

import com.automationrockstars.gir.desktop.ByImage;
import com.automationrockstars.gir.desktop.ImageUiObject;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class SikuliImageUiObject implements ImageUiObject {

	private final Path imagePath;

	private Match wrapped = null;
	private String name = null;
	private double timeout = 5;
	
	public SikuliImageUiObject(String imagePath) {
		this.imagePath = Paths.get(imagePath);
	}

	public Match getWrappedElement(){
		if (wrapped == null){
			wrapped = SikuliDriver.wait(imagePath, timeout);			
		}
		return wrapped;
	}

	public void click() {
		getWrappedElement().click();
	}

	public void sendKeys(CharSequence... keys){
		getWrappedElement().type(Joiner.on("").join(keys));
	}

	public void waitUntilVisible(){
		getWrappedElement();
	}

	public void waitUntilHidden(){
		SikuliDriver.waitUntilInvisible(imagePath, timeout);
	}

	@Override
	public String getText() {
		return getWrappedElement().text();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	@Override
	public Path getLocator() {
		return imagePath;
	}

	@Override
	public Point getLocation() {
		return new Point(getWrappedElement().x, getWrappedElement().y);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(getWrappedElement().getW(), getWrappedElement().getH());
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
		return new SikuliKeyboard(getWrappedElement());
	}

	@Override
	public Mouse getMouse() {
		return new SikuliMouse(getWrappedElement());
	}

	@Override
	public Coordinates getCoordinates() {
		return new Coordinates() {
			
			@Override
			public Point onScreen() {
				return getLocation();
			}
			
			@Override
			public Point onPage() {
				return onScreen();
			}
			
			@Override
			public Point inViewPort() {
				return onScreen();
			}
			
			@Override
			public Object getAuxiliary() {
				return getWrappedElement().toStringShort();
			}
		};
	}

	@Override
	public void submit() {
		click();
		
		
	}

	@Override
	public void clear() {
		getWrappedElement().type(org.sikuli.script.Key.CTRL + "a");
		getWrappedElement().type(org.sikuli.script.Key.DELETE);
		
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
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public boolean isEnabled() {
		throw new IllegalAccessError("Not supported");
	}

	@Override
	public List<WebElement> findElements(By by) {
		return Lists.newArrayList(Iterators.transform(wrap(findAllWrapper(getWrappedElement(), ((ByImage)by).path(), null),((ByImage)by).path(),null),new Function<ImageUiObject, WebElement>() {
			@Override
			public WebElement apply(ImageUiObject input) {
				return input;
			}
		}));
	}

	@Override
	public WebElement findElement(By by) {
		return wrap(findWrapper(getWrappedElement(), ((ByImage)by).path(), null),((ByImage)by).path(),null);
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
		return wrap(findWrapper(getWrappedElement(), imagePath, null),imagePath,null);
	}

	@Override
	public Iterator<ImageUiObject> findElements(ByImage by) {
		return findElements(by.path());
	}

	@Override
	public Iterator<ImageUiObject> findElements(String imagePath) {
		Preconditions.checkArgument(Paths.get(imagePath).toFile().canRead(),"Cannot read file %s",imagePath);
		return wrap(findAllWrapper(getWrappedElement(), imagePath, null),imagePath,null);
	}

	public void highlight(){
		getWrappedElement().highlight(1);
	}

	public static ImageUiObject wrap(Match toWrap, String imagePath,@Nullable String name){
		SikuliImageUiObject result = new SikuliImageUiObject(imagePath);
		result.wrapped = toWrap;
		result.setName(name);
		return result;
		
	}
	
	public static Iterator<ImageUiObject> wrap(Iterator<Match> toWrap, final String imagePath,@Nullable final String name){
		return Iterators.transform(toWrap, new Function<Match, ImageUiObject>() {

			@Override
			public ImageUiObject apply(Match input) {
				System.out.println("wrapping");
				return wrap(input, imagePath, name);
			}
		});
	}
	static Iterator<Match> findAllWrapper(Region where, String locator, @Nullable String name){
		try {
			return where.findAll(locator);
		} catch (FindFailed e) {
			throw new NoSuchElementException(String.format("Element %s not found on the screen", (name==null)?locator:name));
		}
	}
	
	static Match findWrapper(Region where, String locator,@Nullable String name){
		try {
			return where.wait(locator,5);
		} catch (FindFailed e) {
			throw new NoSuchElementException(String.format("Element %s not found on the screen", (name==null)?locator:name));
		}
	}
}
