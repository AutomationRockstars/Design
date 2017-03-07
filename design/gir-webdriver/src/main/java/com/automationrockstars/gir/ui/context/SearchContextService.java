package com.automationrockstars.gir.ui.context;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.openqa.selenium.SearchContext;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class SearchContextService {

	public static SearchContext provideForWeb(){
		return DriverFactory.getDriver();
	}

	public static SearchContext provideForImage(){
		Iterator<SearchContextProvider> providers = ServiceLoader.load(SearchContextProvider.class).iterator();
		System.out.println(providers);
		try {
			SearchContextProvider result = Iterators.find(providers, new Predicate<SearchContextProvider>() {

				@Override
				public boolean apply(SearchContextProvider input) {
					System.out.println(input);
					return input.canProvide(Image.class);
				}
			});
			return result.provide();
		} catch (NoSuchElementException e){
			System.out.println(Lists.newArrayList(ServiceLoader.load(SearchContextProvider.class).iterator()));
			throw new RuntimeException("Cannot find provider for @Image context",e);
		}

	}
}
