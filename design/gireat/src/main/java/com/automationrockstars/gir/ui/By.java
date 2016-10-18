package com.automationrockstars.gir.ui;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface By {

	Class<? extends org.openqa.selenium.By> how();
	String[] using();
	
}
