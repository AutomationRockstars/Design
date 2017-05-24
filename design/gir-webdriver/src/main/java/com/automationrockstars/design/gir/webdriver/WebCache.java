package com.automationrockstars.design.gir.webdriver;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPlugin;
import com.automationrockstars.design.gir.webdriver.plugin.UiDriverPluginService;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
	private static class WebElementResult {

		private final List<WebElement> elements = Lists.newArrayList();
		private final SearchContext ctx;
		private final By by;

		private boolean single;

		public WebElementResult(SearchContext ctx, By by, boolean single){
			this.by = by;
			this.ctx = ctx;
			this.single = single;
			if (single){
				try {
					elements.add(ctx.findElement(by));
				} catch (WebDriverException ignore){}
			} else {
				try {
					elements.addAll(ctx.findElements(by));
				} catch (Exception ignore){
									
				}
			}
		}

		public SearchContext context(){
			return ctx;
		}

		public By by(){
			return by;
		}

		public boolean hasElements(){
			return ! elements.isEmpty();
		}
		public List<WebElement> multiple(){
			if (single){
				try {
					elements.addAll(ctx.findElements(by));
				} catch (WebDriverException ignore){}
			}
			return elements;
		}

		public WebElement single(){
			return (elements.isEmpty())?null:elements.get(0);
		}


	}

	private static final ThreadLocal<Map<SearchContext,Map<By,WebElementResult>>> webCache = new ThreadLocal<Map<SearchContext,Map<By,WebElementResult>>>(){

		@Override
		protected Map<SearchContext,Map<By,WebElementResult>> initialValue(){
			return Maps.newHashMap();
		}

	};


	private static final ThreadLocal<Entry<SearchContext,By>> lastQuery = new ThreadLocal<>();
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

	private  void makeValid(WebElementResult result, boolean single){
		try {
			if (single){
				result.single().getTagName();
			} else {
				for (WebElement el : result.multiple()){
					el.getTagName();
				}
			}
		} catch (WebDriverException e){
			invalidateElement(result.context(), result.by());
			find(result.context(),result.by(),single);
		}
	} 

	private WebElementResult find(SearchContext s, By by, boolean single){

		Map<By,WebElementResult> els = webCache.get().get(s);
		boolean valid = ! ConfigLoader.config().getBoolean("webcache.validate",true);
		if (els == null){
			els = Maps.newHashMap();
			webCache.get().put(s, els);
			els.put(by,new WebElementResult(s, by, single));
			valid = true;
		} else {
			WebElementResult targets = els.get(by);			
			if (targets == null  || ! targets.hasElements()
					|| isRepeated(s, by)){
				targets = new WebElementResult(s, by, single);
				els.put(by, targets);
				valid = true;
			} 
		} 
		if (! valid){
			makeValid(webCache.get().get(s).get(by), single);
		}
		try {
		return webCache.get().get(s).get(by);
		} catch (Throwable e){
			throw new NoSuchElementException(String.format("Element %s cannot be find inside %s", by,s));
		}
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
		return cacheFinders.get().find(s, by,false).multiple();
	}

	public static List<WebElement> findElements(SearchContext ctx, By by) {
		return cacheFinders.get().find(ctx, by,false).multiple();
	}

	public static WebElement findElement(SearchContext ctx, By by) {
		return cacheFinders.get().find(ctx, by,true).single();
	}

	public static synchronized void invalidateElements(SearchContext ctx, By by){
		webCache.get().get(ctx).remove(by);
	}

	public static void invalidateElement(SearchContext ctx,By by){
		webCache.get().get(ctx).remove(by);
	}

}
