package com.automationrockstars.gir.desktop.internal;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gir.desktop.ByImage;
import com.automationrockstars.gir.desktop.ImageSearchContext;
import com.automationrockstars.gir.desktop.ImageUiObject;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class SikuliDriver implements ImageSearchContext {

	private final static SikuliDriver instance = new SikuliDriver();
	private SikuliDriver() {
	}
	public static SikuliDriver driver(){
		return instance;
	}
	
	
	
	static Region screen() {
		return screen;
	}
	private static final Screen screen = new Screen(1);
	
	private static final double TIMEOUT = ConfigLoader.config().getDouble("imagedriver.default.timeout",5);
	
//	public static Match find(Path image) throws FindFailed{
//				return screen.find(image.toString());		
//	}
//	
//	public static Iterator<Match> findAll(Path image){
//		try {
//			return screen.findAll(image.toString());
//		} catch (FindFailed e) {
//			throw new NoSuchElementException(String.format("Element identified by image %s not found on the screen", image)); 
//		}
//	}
//	
	public static Match wait(Path image){
		return wait(image, TIMEOUT);
	}
	
	public static Match wait(Path image, double timeout){
		try {
			return screen.wait(image.toString(), timeout);
		} catch (FindFailed e) {
			throw new NoSuchElementException(String.format("Element identified by image %s not found on the screen within %s seconds", image,TIMEOUT));
		}
	}
	
	public static void waitUntilInvisible(Path image, double timeout){		
			Preconditions.checkState(screen.waitVanish(image.toString(), timeout),"Element identified by image %s ");
	}
	
	public static Match isVisible(Path image){
		return screen.exists(image.toString());
	}

	@Override
	public List<WebElement> findElements(By by) {
		Preconditions.checkArgument(by instanceof ByImage);
		return Lists.newArrayList(Iterators.transform(findElements(((ByImage)by).path()),new Function<ImageUiObject, WebElement>() {

			@Override
			public WebElement apply(ImageUiObject input) {
				return input;
			}
		}));
	}

	@Override
	public WebElement findElement(By by) {
		Preconditions.checkArgument(by instanceof ByImage);
		return findElement(((ByImage)by).path());
	}

	@Override
	public ImageUiObject findElement(ByImage by) {
		return findElement(by.path());
	}

	@Override
	public ImageUiObject findElement(String imagePath) {
		return SikuliImageUiObject.wrap(SikuliImageUiObject.findWrapper(screen, imagePath, null),imagePath,null);
	}

	@Override
	public Iterator<ImageUiObject> findElements(ByImage by) {
		return findElements(by.path());
	}

	@Override
	public Iterator<ImageUiObject> findElements(String imagePath) {
		return SikuliImageUiObject.wrap(SikuliImageUiObject.findAllWrapper(screen, imagePath, null),imagePath,null);
	}
	
	
}
