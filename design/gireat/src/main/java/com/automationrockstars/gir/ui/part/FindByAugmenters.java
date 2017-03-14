package com.automationrockstars.gir.ui.part;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.By.ByCssSelector;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.By.ByName;
import org.openqa.selenium.By.ByPartialLinkText;
import org.openqa.selenium.By.ByTagName;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.support.ByIdOrName;
import org.openqa.selenium.support.How;

import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.FindByAugmenter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import static org.openqa.selenium.support.How.*;
import static com.google.common.base.Strings.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
public class FindByAugmenters {

	public static FindByAugmenter instance(Class<? extends FindByAugmenter> value) {
		try {
			return value.newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			Throwables.propagate(e);
			return null;
		}
	}

	public static How how(FindBy toBeAugmented) {
		if (toBeAugmented.how() != null  && (! toBeAugmented.how().equals(How.UNSET))){
			return toBeAugmented.how();
		} else if (! isNullOrEmpty(toBeAugmented.id())){
			return ID;
		} else if (! isNullOrEmpty(toBeAugmented.className())){
			return CLASS_NAME;
		} else if (! isNullOrEmpty(toBeAugmented.css())){
			return CSS;
		} else if (! isNullOrEmpty(toBeAugmented.linkText())){
			return LINK_TEXT;
		} else if (! isNullOrEmpty(toBeAugmented.name())){
			return NAME;
		} else if (! isNullOrEmpty(toBeAugmented.partialLinkText())){
			return How.PARTIAL_LINK_TEXT;
		} else if (! isNullOrEmpty(toBeAugmented.xpath())){
			return XPATH;
		} else if (! isNullOrEmpty(toBeAugmented.tagName())){
			return TAG_NAME;
		}  else return How.UNSET;
	}
	
	public static String using(FindBy toBeAugmented){
		if (! Strings.isNullOrEmpty(toBeAugmented.using())){
			return toBeAugmented.using();
		} else if (! isNullOrEmpty(toBeAugmented.id())){
			return toBeAugmented.id();
		} else if (! isNullOrEmpty(toBeAugmented.className())){
			return toBeAugmented.className();
		} else if (! isNullOrEmpty(toBeAugmented.css() )){
			return toBeAugmented.css();
		} else if (! isNullOrEmpty(toBeAugmented.linkText())){
			return toBeAugmented.linkText();
		} else if (! isNullOrEmpty(toBeAugmented.name())){
			return toBeAugmented.name();
		} else if (! isNullOrEmpty(toBeAugmented.partialLinkText())){
			return toBeAugmented.partialLinkText();
		} else if (! isNullOrEmpty(toBeAugmented.xpath() )){
			return toBeAugmented.xpath();
		} else if (! isNullOrEmpty(toBeAugmented.tagName() )){
			return toBeAugmented.tagName();
		}  else return null;
	}
	
	public static Class<? extends By> classFor(FindBy toBeAugmented){
		switch (how(toBeAugmented)) {
		case ID:
			return ById.class;
		case CLASS_NAME:
			return ByClassName.class;
		case CSS:
			return ByCssSelector.class;
		case ID_OR_NAME:
			return ByIdOrName.class;
		case LINK_TEXT:
			return ByLinkText.class;
		case NAME:
			return ByName.class;
		case PARTIAL_LINK_TEXT:
			return ByPartialLinkText.class;
		case TAG_NAME:
			return ByTagName.class;
		case XPATH:
			return ByXPath.class;
		default:
			return null;
		}
	}
	
	public static Constructor<? extends By> constructorFor(FindBy toBeAugmented){
		try {
			final Class<? extends By> byClass = classFor(toBeAugmented);
			Preconditions.checkArgument(byClass != null, "Cannot find class for FindBy %s",toBeAugmented);
			return byClass.getConstructor(String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			Throwables.propagate(e);
			return null;
		} 
	}
	
	public static By instanceForValue(FindBy toBeAugmented, String newValue){
		try {
			return constructorFor(toBeAugmented).newInstance(newValue);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Throwables.propagate(e);
			return null;
		}
	}

}
