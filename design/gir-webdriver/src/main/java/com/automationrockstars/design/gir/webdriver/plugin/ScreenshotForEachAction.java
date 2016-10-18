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
package com.automationrockstars.design.gir.webdriver.plugin;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.automationrockstars.asserts.Asserts;
import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.AllureStoryReporter;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.design.gir.webdriver.UiObject;
import com.automationrockstars.design.gir.webdriver.plugin.UiObjectActionPlugin;
import com.automationrockstars.design.gir.webdriver.plugin.UiObjectFindPlugin;

public class ScreenshotForEachAction implements UiObjectActionPlugin, UiObjectFindPlugin{

	private static synchronized void attach(String name){
		AllureStoryReporter.attachScreenshot(name);
		display(name);
	}
	private static final JFrame frame = new JFrame();
	private static final JLabel pic = new JLabel();
	private static final boolean debug = ConfigLoader.config().getBoolean("webdriver.debug",false); 
	private static int w = 0;
	private static int h = 0;
	static {
		if (debug){
			w = new Double(DriverFactory.getDriver().manage().window().getSize().width*0.75).intValue();
			h = new Double(DriverFactory.getDriver().manage().window().getSize().height*0.75).intValue();
			frame.setSize(w,h);
			frame.add(pic);
			frame.setVisible(true);
			display("Starting");
			
		}
	}
	
	private static final void display(String name){
		if (debug){
			byte[] image = Asserts.makeScreenshotIfPossible();

			if (image != null && frame.isVisible()){
				Image img = Toolkit.getDefaultToolkit().createImage(image);
				ImageIcon ic = new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH),name);
				pic.setIcon(ic);
				frame.setTitle(name);
			}
		}
	}
	private static synchronized final boolean isEnabled() {
		return ConfigLoader.config().getBoolean("webdriver.all.screenshots.enabled",true);
	}


	private static final AtomicLong scount = new AtomicLong();
	@Override
	public void beforeClick(UiObject element) {
		if (isEnabled()){
			attach("bclick"+"_"+scount.incrementAndGet()+"_"+element);
		}

	}

	@Override
	public void afterClick(UiObject element) {
		if (isEnabled())
			attach("aclick"+""+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void beforeSubmit(UiObject element) {
		if (isEnabled())
			attach("bsubmit"+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void afterSubmit(UiObject element) {
		if (isEnabled())
			attach("asubmit"+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void beforeSendKeys(UiObject element, CharSequence... keysToSend) {
		if (isEnabled())
			attach("bkeys"+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void afterSendKeys(UiObject element, CharSequence... keysToSend) {
		if (isEnabled())
			attach("akeys"+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void beforeClear(UiObject element) {
		if (isEnabled())
			attach("bclear"+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void afterClear(UiObject element) {
		if (isEnabled())
			attach("aclear"+"_"+scount.incrementAndGet()+"_"+element); 
	}

	@Override
	public void beforeFindElements(UiObject element, By by) {
		if (isEnabled())
			attach("bfind"+"_"+scount.incrementAndGet()+"_"+by);
	}

	@Override
	public void afterFindElements(UiObject element, By by, List<WebElement> result) {
		if (isEnabled())
			attach("afind"+"_"+scount.incrementAndGet()+"_"+element);
	}

	@Override
	public void beforeFindElement(UiObject element, By by) {
		if (isEnabled())
			attach("bfind"+"_"+scount.incrementAndGet()+"_"+by);

	}

	@Override
	public void afterFindElement(UiObject element, By by, WebElement result) {
		if (isEnabled())
			attach("afind"+"_"+scount.incrementAndGet()+"_"+element);

	}

	@Override
	public void beforeWaitForVisible(UiObject element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterWaitForVisible(UiObject element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeWaitForPresent(UiObject element) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterWaitForPresent(UiObject element) {
		// TODO Auto-generated method stub

	}

}
