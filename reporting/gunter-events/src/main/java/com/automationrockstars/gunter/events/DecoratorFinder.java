/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

package com.automationrockstars.gunter.events;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import eu.infomas.annotation.AnnotationDetector;
import eu.infomas.annotation.AnnotationDetector.MethodReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class DecoratorFinder {

    private static List<Method> decorators = Lists.newArrayList();
    private static Logger LOG = LoggerFactory.getLogger(DecoratorFinder.class);

    static {
        AnnotationDetector detector = new AnnotationDetector(new DecoratorReporter());
        try {
            detector.detect();
        } catch (IOException e) {
            LOG.error("Cannot execute detection for {}", DecoratorFinder.class);
        }
    }

    public static Event decorate(Event event) {
        for (Method decorator : decorators) {
            try {
                event = (Event) decorator.invoke(null, event);
            } catch (Throwable e) {
                LOG.error("Decorator {} failed on event {}", decorator, event);
            }
        }
        return event;
    }

    public static class DecoratorReporter implements MethodReporter {

        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends Annotation>[] annotations() {
            return new Class[]{EventDecorator.class};
        }

        @Override
        public void reportMethodAnnotation(Class<? extends Annotation> annotation, String className,
                                           String methodName) {
            try {
                for (Method method : Class.forName(className).getMethods()) {
                    Preconditions.checkArgument(Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()), "Decorator method needs to be public static");
                    Preconditions.checkArgument(Event.class.isAssignableFrom(method.getReturnType()), "Decorator method needs to return TestEvent");
                    Preconditions.checkArgument(method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]), "Decorator method needs to accept one argument of type TestEvent");
                    decorators.add(method);
                }
            } catch (SecurityException | ClassNotFoundException | IllegalArgumentException e) {
                LOG.error("Cannot use decorator method {} due to {}", methodName, e.toString());
            }

        }

    }
}
