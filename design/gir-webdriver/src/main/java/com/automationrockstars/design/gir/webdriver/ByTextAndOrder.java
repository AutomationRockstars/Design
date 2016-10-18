package com.automationrockstars.design.gir.webdriver;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ByTextAndOrder {

	public static OrderBuilder byTextAndOrder(By typeOfElements){
		return new OrderBuilder(typeOfElements);
	}
	public static class OrderBuilder extends By {

		
		private By type ;
		private OrderBuilder(By typeOfElements){
			type = typeOfElements;
		}
		private String after;
		private String before;
		private String text;

		public OrderBuilder after(String text){
			this.after = text;
			return this;
		}

		public OrderBuilder before(String text){
			this.before = text;
			return this;
		}

		public OrderBuilder withText(String text){
			this.text = text;
			return this;
		}
		
		private boolean first = false;
		public OrderBuilder first(){
			first = true;
			return this;
		}
		
		private boolean last = false;
		public OrderBuilder last(){
			last = true;
			return this;
		}

		private WebElement findOnList(List<WebElement> allElements, final String text){
			Optional<WebElement> element = Iterables.tryFind(allElements, new Predicate<WebElement>(){

				@Override
				public boolean apply(WebElement input) {
					try {
						String content = input.getText();
						return  (! Strings.isNullOrEmpty(content)) && content.contains(text);
					} catch (Throwable t){
						return false;
					}
					
					
				}

			});
			if (! element.isPresent()){
				throw new NoSuchElementException("Element secified not found " + text + " " + type);
			}
			return element.get();
		}
		@Override
		public List<WebElement> findElements(SearchContext context) {		
			List<WebElement> allElements = context.findElements(type);
			if (! Strings.isNullOrEmpty(after)){
				int topCap = allElements.indexOf(findOnList(allElements, after));
				allElements = allElements.subList(topCap, allElements.size()-1);
			}
			if (! Strings.isNullOrEmpty(before)){
				int bottomCap = allElements.indexOf(findOnList(allElements, before));
				allElements = allElements.subList(0, bottomCap);
			}
			List<WebElement> result = Lists.newArrayList();
			boolean keepFinding = true;
			while (keepFinding){
				try {
					result.add(findOnList(allElements, text));
					allElements = allElements.subList(allElements.indexOf(result.get(result.size()-1)), allElements.size()-1);
					keepFinding = allElements.size() > 0;
				} catch (NoSuchElementException done){
					keepFinding = false;
				}
			}
			if (result.isEmpty()){
				throw new NoSuchElementException("Elements " + this.toString() + " not found");
			}
			
			if (first){
				result = result.subList(0, 0);
			} else if (result.size() > 1 && last){
				result = result.subList(result.size()-2, result.size()-1);
			}
			return result;
		}

		public String toString(){
			StringBuilder builder = new StringBuilder(" identified by " + type);
			if (! Strings.isNullOrEmpty(text)){
				builder.append(" with text " + text);
			}
			if (! Strings.isNullOrEmpty(before)){
				builder.append(" before text " + before);
			}
			if (! Strings.isNullOrEmpty(after)){
				builder.append(" after text " + after);
			}
			if (first){
				builder.append(" first on the list");
			} else if (last) {
				builder.append(" last on the list");
			}
			return builder.toString();
		}

	}

}
