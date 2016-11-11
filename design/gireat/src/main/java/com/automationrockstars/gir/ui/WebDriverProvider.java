package com.automationrockstars.gir.ui;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebDriverProvider {

	boolean cacheable() default true;
}
