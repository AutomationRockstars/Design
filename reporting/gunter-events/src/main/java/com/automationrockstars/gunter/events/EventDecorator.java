package com.automationrockstars.gunter.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.automationrockstars.gunter.EventType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventDecorator {

	EventType eventType() default EventType.ALL;
}
