package com.automationrockstars.design.gir.webdriver.el;

import java.util.Map;

import org.mvel2.integration.VariableResolver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Maps;

public class WebElementVariableResolver  implements VariableResolver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1725557044733700005L;
	private final WebElement element;
	private final String name;
	private final Map<String,Object> cached = Maps.newHashMap();
	public WebElementVariableResolver(WebElement element, String name){
		this.element = element;
		this.name = name;
	}
	public String getName() {
		return name;
	}

	@SuppressWarnings("rawtypes")
	public Class getType() {
		return WebElement.class;
	}

	@SuppressWarnings("rawtypes")
	public void setStaticType(Class type) {
		System.out.println("set class " + type);
	}

	public int getFlags() {
		return 0;
	}

	public Object getValue(){
		if (cached.get(name) == null){
			cached.put(name, getOrigValue());
		}
		return cached.get(name);
	}
	public Object getOrigValue() {
		if (name == null){
			return null;
		}
		try {
		switch (name.toLowerCase()) {
		case "text":
			return element.getText();
		case "tag":
			return element.getTagName();
		case "size":
			return element.getSize();
		case "rect":
			return element.getRect();
		case "displayed":
			return element.isDisplayed();
		case "enabled":
			return element.isEnabled();
		case "selected":
			return element.isSelected();
		case "element":
			return element;
		case "find":
			return new Finder();
		default:
			try {
			return  element.getAttribute(name);
			} catch (Throwable e) {
				try {
				return element.getCssValue(name);
				} catch (Throwable ee){
					return null;
				}
			}			
		}
		} catch (Throwable someIssue){
			return null;
		}
	}

	public void setValue(Object value) {
		System.out.println("set " + value);
	}

}
