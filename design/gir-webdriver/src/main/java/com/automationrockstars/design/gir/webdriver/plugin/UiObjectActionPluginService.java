/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.design.gir.webdriver.plugin;

import java.util.List;
import java.util.ServiceLoader;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UiObjectActionPluginService {

	public static class CompositeActionPlugin implements UiObjectActionPlugin{

		public void beforeClick(UiObject element) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.beforeClick(element);
			}
		}

		public void afterClick(UiObject element) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.afterClick(element);
			}

		}

		public void beforeSubmit(UiObject element) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.beforeSubmit(element);
			}

		}

		public void afterSubmit(UiObject element) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.afterSubmit(element);
			}
		}

		public void beforeSendKeys(UiObject element, CharSequence... keysToSend) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.beforeSendKeys(element,keysToSend);
			}

		}

		public void afterSendKeys(UiObject element, CharSequence... keysToSend) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.afterSendKeys(element,keysToSend);
			}
		}

		public void beforeClear(UiObject element) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.beforeClear(element);
			}

		}

		public void afterClear(UiObject element) {
			for (UiObjectActionPlugin plugin : plugins){
				plugin.afterClear(element);
			}

		}

	}

	private static CompositeActionPlugin instance = new CompositeActionPlugin();
	private static List<UiObjectActionPlugin> plugins = Lists.newArrayList();
	static {
		registerSpiPlugins();
	}

	public static UiObjectActionPlugin actionPlugins(){
		return instance;
	}

	public static void registerPlugin(UiObjectActionPlugin plugin){
		plugins.add(plugin);
	}

	public static List<UiObjectActionPlugin> getPlugins(){
		return ImmutableList.copyOf(plugins);
	}

	private static void registerSpiPlugins(){
		plugins.addAll(Lists.newArrayList(ServiceLoader.load(UiObjectActionPlugin.class).iterator()));
	}
}
