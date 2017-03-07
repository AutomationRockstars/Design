package com.automationrockstars.gir.ui.part;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.context.SearchContextService;
import com.google.common.collect.FluentIterable;

import ru.yandex.qatools.htmlelements.element.Link;

public class ImageUiPartDelegate extends AbstractUiPartDelegate{

	public ImageUiPartDelegate(Class<? extends UiPart> view) {
		super(view);
		
	}
	
	public ImageUiPartDelegate(Class<? extends UiPart> view, UiObject toWrap) {
		super(view, toWrap);
		
	}

	@Override
	public FluentIterable<UiObject> children() {
		
		return null;
	}

	@Override
	public boolean has(By element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasText(String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void waitForHidden() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UiObject childWithText(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Link childLinkWithText(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebElement getWrappedElement() {
		wrapped = (UiObject) SearchContextService.provideForImage().findElement(getLocator());
		return wrapped;
	}

}
