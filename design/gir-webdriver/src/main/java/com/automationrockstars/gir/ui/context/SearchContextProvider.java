package com.automationrockstars.gir.ui.context;

import java.lang.annotation.Annotation;

import org.openqa.selenium.SearchContext;

public interface SearchContextProvider {

	boolean canProvide(Class<? extends Annotation> context);
	
	SearchContext provide();
	
}
