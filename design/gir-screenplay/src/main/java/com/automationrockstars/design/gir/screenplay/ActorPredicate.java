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

package com.automationrockstars.design.gir.screenplay;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import javassist.Modifier;
import org.apache.commons.lang.ArrayUtils;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

public interface ActorPredicate extends Predicate<String> {

    static class CompositePredicate implements Predicate<String> {

        private static List<ActorPredicate> predicates = null;

        private static List<ActorPredicate> predicates() {
            if (predicates == null) {
                predicates = Lists.newArrayList();
                String[] predicatePackages = ConfigLoader.config().getStringArray("actor.predicates");
                if (ArrayUtils.isEmpty(predicatePackages)) {
                    predicatePackages = new String[]{"com.automationrockstars"};
                }
                Reflections r = new Reflections((Object[]) predicatePackages);
                Set<Class<? extends ActorPredicate>> predicateClasses = r.getSubTypesOf(ActorPredicate.class);
                for (Class<? extends ActorPredicate> predicate : predicateClasses) {

                    try {
                        if (!Modifier.isAbstract(predicate.getModifiers())) {
                            ActorPredicate instance = predicate.newInstance();
                            predicates.add(instance);
                        }
                    } catch (Exception ignore) {

                    }
                }
            }
            return predicates;
        }

        public static Predicate<String> instance() {
            return new CompositePredicate();
        }

        @Override
        public boolean apply(String input) {
            boolean result = true;
            for (ActorPredicate predicate : predicates()) {
                result = result && predicate.apply(input);
            }
            return result;
        }
    }
}
