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
				try {
					plugin.beforeGetDriver();
				} catch (Throwable e){}
			}

		}

		@Override
		public void afterGetDriver(WebDriver driver) {
			for (UiDriverPlugin plugin : plugins){
				try {
					plugin.afterGetDriver(driver);
				} catch (Throwable e){
					e.printStackTrace();
				}
			}

		}

		@Override
		public void beforeCloseDriver(WebDriver driver) {
			for (UiDriverPlugin plugin : plugins){
				try {
				plugin.beforeCloseDriver(driver);
				} catch (Throwable e){}
			}

		}

		@Override
		public void afterCloseDriver() {
			for (UiDriverPlugin plugin : plugins){
				try {plugin.afterCloseDriver();
				} catch (Throwable e){}
				
			}

		}

		@Override
		public void beforeInstantiateDriver() {
			for (UiDriverPlugin plugin : plugins){
				try {
					plugin.beforeInstantiateDriver();
				}  catch (Throwable e){}
				
			}
			
		}
		
		public void afterInstantiateDriver(WebDriver driver){
			for (UiDriverPlugin plugin : plugins){
				try {
					plugin.afterInstantiateDriver(driver);
				}  catch (Throwable e){}
				
			}
		}

	}


	private static final CompositeUiDriverPlugin instance = new CompositeUiDriverPlugin();
	private static final List<UiDriverPlugin> plugins = Lists.newCopyOnWriteArrayList();
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
