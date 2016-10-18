package com.automationrockstars.design.gir.webdriver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitialPage {

	String url() default "";
	boolean reload() default false;
	boolean restartBrowser() default false;
	
	
}
