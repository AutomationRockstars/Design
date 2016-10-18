package com.automationrockstars.gir.ui;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {

	String condition() default "";
	int timeout() default 0;
}
