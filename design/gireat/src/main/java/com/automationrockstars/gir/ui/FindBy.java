package com.automationrockstars.gir.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openqa.selenium.support.How;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FindBy {
	  How how() default How.UNSET;

	  String using() default "";

	  String id() default "";

	  String name() default "";

	  String className() default "";

	  String css() default "";

	  String tagName() default "";

	  String linkText() default "";

	  String partialLinkText() default "";

	  String xpath() default "";
}
