package com.automationrockstars.design.gir.webdriver;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPlugin;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPluginService;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

public class WebCache  {
	private final static CacheCleaner cleaner = new CacheCleaner();
	static {
		UiDriverPluginService.registerPlugin(cleaner);
	}
	
	static class CacheCleaner implements  UiDriverPlugin{ 
		@Override
		public void beforeInstantiateDriver() {
		}

		@Override
		public void beforeGetDriver() {
		}

		@Override
		public void afterGetDriver(WebDriver driver) {
		}

		@Override
		public void beforeCloseDriver(WebDriver driver) {
			
		}

		@Override
		public void afterCloseDriver() {
			webCache.remove();
		}

		@Override
		public void afterInstantiateDriver(WebDriver driver) {		
		}
	}

	private static final ThreadLocal<Map<SearchContext,Map<By,SoftReference<List<WebElement>>>>> webCache = new ThreadLocal<Map<SearchContext,Map<By,SoftReference<List<WebElement>>>>>(){

		@Override
		protected Map<SearchContext,Map<By,SoftReference<List<WebElement>>>> initialValue(){
			return Maps.newConcurrentMap();
		}

	};
	
	
	private static final ThreadLocal<Entry<SearchContext,By>> lastQuery = new ThreadLocal();
	private static final ThreadLocal<Integer> lastQueryCount = new ThreadLocal<>();
	private static final Integer REPEAT_TRESHOLD = ConfigLoader.config().getInteger("webcache.max.repeat", 10); 
	private boolean isRepeated(SearchContext s, By by){
	if (lastQuery.get() != null && lastQuery.get().getKey().equals(s) && lastQuery.get().getValue().equals(by)){
			if (lastQueryCount.get() > REPEAT_TRESHOLD){
				return true;
			} else {
				lastQueryCount.set(lastQueryCount.get()+1);
				return false;
			}
		} else {
			lastQuery.set(Collections.singletonMap(s, by).entrySet().iterator().next());
			lastQueryCount.set(0);
			return false;
		}
	}
	private List<WebElement> find(SearchContext s, By by){
		Map<By,SoftReference<List<WebElement>>> els = webCache.get().get(s);
		boolean valid = ! ConfigLoader.config().getBoolean("webcache.validate",true);
		if (els == null){
			els = Maps.newConcurrentMap();
			els.put(by,new SoftReference(s.findElements(by)));
			webCache.get().put(s, els);
			valid = true;
		} else {
			SoftReference<List<WebElement>> targets = els.get(by);			
			if (targets == null || targets.get() == null || targets.get().isEmpty()
					|| isRepeated(s, by)){
				targets = new SoftReference(s.findElements(by));
				els.put(by, targets);
				valid = true;
			} 
		} 
		if (! valid){
			FluentIterable<WebElement> result = FluentIterable.from(webCache.get().get(s).get(by).get());
			result = result.filter(new Predicate<WebElement>() {
				@Override
				public boolean apply(WebElement input) {
					try {
						return input.getTagName() != null;
					} catch (WebDriverException e){
						return false;
					}
				}				
			});
			if (result.isEmpty()){
				els.put(by,new SoftReference( s.findElements(by)));
				valid = true;
			} 
		}
		return webCache.get().get(s).get(by).get();
	}

	private WebCache(){

	}
	private static final ThreadLocal<WebCache> cacheFinders = new InheritableThreadLocal<WebCache>(){
		@Override
		protected WebCache initialValue(){
			return new WebCache();
		}
	};
	public static List<WebElement> fromCache(SearchContext s,By by){
		return cacheFinders.get().find(s, by);
	}

}
