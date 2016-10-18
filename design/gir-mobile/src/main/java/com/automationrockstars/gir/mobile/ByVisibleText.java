/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gir.mobile;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.parboiled.common.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public abstract class ByVisibleText extends By {

	protected String[] filter;
	protected boolean pattern = false;
	
	private static final Logger LOG = LoggerFactory.getLogger(ByVisibleText.class);
	
	public ByVisibleText(String... filter){
		this.filter=filter;
	}
	
	public String toString(){
		return String.format("By.visibleText : %s", ArrayUtils.toString(filter));
	}
	
	public String[] getFilter(){
		return filter;
	}
	
	public static ByVisibleText visibleText(String... filter){
		if (MobileFactory.currentPlatform().equals(MobileFactory.ANDROID)){
			return new ByVisibleTextAndroid(filter);
		} else if (MobileFactory.currentPlatform().equals(MobileFactory.IOS)){
			return new ByVisibleTextIos(filter); 
		} 
		Preconditions.checkState(false,"Platform cannot be determined");
		return null;
	}
	
	public static ByVisibleText visibleTextPattern(String regex){		
		ByVisibleText result = visibleText(String.format(".*%s.*",regex));
		result.pattern = true;
		return result;
	}
	
	public abstract List<WebElement> findElements(final SearchContext context, final List<String> lines);
	
	@Override
	public List<WebElement> findElements(final SearchContext context) {
		List<String> lines = lines();
		
		if (lines.isEmpty()){
			LOG.debug("{} not in page source",Arrays.toString(getFilter()));
		}
		return findElements(context, lines);
	}
	protected List<String> lines(){
		if (pattern){
			return PageUtils.getLinesMatching(getFilter()[0]);
		} else {
			return PageUtils.getLinesContaining(getFilter());
		}
	}
	@Override
	public WebElement findElement(SearchContext context){
		List<String> lines = lines(); 
		if (lines.isEmpty()){
			throw new NoSuchElementException(String.format("Element searched by %s not found", Arrays.toString(getFilter())));
		}
		List<WebElement> result = findElements(context,Lists.newArrayList(lines.get(0)));
		if (result.isEmpty()){
			throw new NoSuchElementException(String.format("Element searched by %s not found", Arrays.toString(getFilter())));
		}
		return result.get(0);
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof ByVisibleText){
			String[] theirs = (String[]) ArrayUtils.removeElement(((ByVisibleText) o).getFilter(),ByVisibleTextIos.VISIBLE);
			String[] ours = (String[]) ArrayUtils.removeElement(getFilter(),ByVisibleTextIos.VISIBLE);
			return ArrayUtils.isEquals(theirs, ours);
		} else return false;
		
		
	}
}
