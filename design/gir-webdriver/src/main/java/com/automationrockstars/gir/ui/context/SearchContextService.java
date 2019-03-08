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

package com.automationrockstars.gir.ui.context;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import org.openqa.selenium.SearchContext;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class SearchContextService {

    public static SearchContext provideForWeb() {
        return DriverFactory.getDriver();
    }

    public static SearchContext provideForImage() {
        Iterator<SearchContextProvider> providers = ServiceLoader.load(SearchContextProvider.class).iterator();
        try {
            SearchContextProvider result = Iterators.find(providers, new Predicate<SearchContextProvider>() {

                @Override
                public boolean apply(SearchContextProvider input) {
                    return input.canProvide(Image.class);
                }
            });
            return result.provide();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Cannot find provider for @Image context", e);
        }

    }
}
