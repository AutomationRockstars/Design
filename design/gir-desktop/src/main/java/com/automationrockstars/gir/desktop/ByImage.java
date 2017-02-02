package com.automationrockstars.gir.desktop;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class ByImage extends By {

	@Override
	public List<WebElement> findElements(SearchContext context) {
		Preconditions.checkArgument(ImageSearchContext.class.isAssignableFrom(context.getClass()), "ByImage works only with SikuliDriver");
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
		this.imagePath = imagePath;
	}
	
	public String path(){
		return imagePath;
	}

}
