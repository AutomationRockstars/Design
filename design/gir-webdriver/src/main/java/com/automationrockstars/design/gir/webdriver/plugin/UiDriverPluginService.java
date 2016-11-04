package com.automationrockstars.design.gir.webdriver.plugin;

import java.util.List;
import java.util.ServiceLoader;

import org.openqa.selenium.WebDriver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UiDriverPluginService {

	public static class CompositeUiDriverPlugin implements UiDriverPlugin {

		@Override
		public void beforeGetDriver() {
			for (UiDriverPlugin plugin : plugins){
				plugin.beforeGetDriver();
			}

		}

		@Override
		public void afterGetDriver(WebDriver driver) {
			for (UiDriverPlugin plugin : plugins){
				plugin.afterGetDriver(driver);	
			}

		}

		@Override
		public void beforeCloseDriver(WebDriver driver) {
			for (UiDriverPlugin plugin : plugins){
				plugin.beforeCloseDriver(driver);
			}

		}

		@Override
		public void afterCloseDriver() {
			for (UiDriverPlugin plugin : plugins){
				plugin.afterCloseDriver();
			}

		}

		@Override
		public void beforeInstantiateDriver() {
			for (UiDriverPlugin plugin : plugins){
				plugin.beforeInstantiateDriver();
			}
			
		}

	}


	private static CompositeUiDriverPlugin instance = new CompositeUiDriverPlugin();
	private static List<UiDriverPlugin> plugins = Lists.newArrayList();
	static {
		registerSpiPlugins();
	}

	public static CompositeUiDriverPlugin driverPlugins(){
		return instance;
	}

	public static void registerPlugin(UiDriverPlugin plugin){
		plugins.add(plugin);
	}

	public static List<UiDriverPlugin> getPlugins(){
		return ImmutableList.copyOf(plugins);
	}

	private static void registerSpiPlugins(){
		plugins.addAll(Lists.newArrayList(ServiceLoader.load(UiDriverPlugin.class).iterator()));
	}
}
