package com.automationrockstars.gir.desktop;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.automationrockstars.gir.desktop.internal.ImageCache;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class ByImage extends By {

	@Override
	public List<WebElement> findElements(SearchContext context) {
		Preconditions.checkArgument(ImageSearchContext.class.isAssignableFrom(context.getClass()) || context.toString().toLowerCase().contains("sikuli"), "ByImage works only with SikuliDriver");
		if (context instanceof RemoteWebDriver) return searchRemote(imagePath, context);
		List<WebElement> result = Lists.newArrayList(Iterators.transform(((ImageSearchContext)context).findElements(imagePath), new Function<ImageUiObject, WebElement>() {
			@Override
			public WebElement apply(ImageUiObject input) {
				return input;
			}
		}));
		return result;
		
	}
	
	private String imagePath;
	public ByImage(String imagePath){
		if (isImageId(imagePath)){
			imagePath = translateFromId(imagePath);
		}
		this.imagePath = imagePath;
	}
	
	public String path(){
		return imagePath;
	}
	
	private List<WebElement> searchRemote(String imagePath, SearchContext ctx){
		if (WebDriver.class.isAssignableFrom(ctx.getClass())){
			ImageCache.syncRemote((WebDriver) ctx);
		}
		return ((FindsById)ctx).findElementsById(translateToId(imagePath));
	}
	
	public static boolean isImageId(String id){
		return id.startsWith("image:");
	}
	
	public static String translateFromId(String id){
		Preconditions.checkArgument(isImageId(id),"id %s is not image id",id);
		return id.replace("image:","");
	}
	
	public static String translateToId(String imagePath){
		return String.format("image:%s", imagePath);
	}
	
	public String toString(){
		return String.format("element matching image %s", imagePath);
	}

}
