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

package com.automationrockstars.gir.ui;

import com.automationrockstars.design.gir.webdriver.UiObject;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

public class ListOfUiObjects implements Iterable<UiObject> {

    private FluentIterable<UiObject> internal;

    private ListOfUiObjects(List<UiObject> internal) {
        this.internal = FluentIterable.from(internal);
    }

    public static ListOfUiObjects from(List<UiObject> of) {
        return new ListOfUiObjects(of);
    }

    @Override
    public Iterator<UiObject> iterator() {
        return Iterators.unmodifiableIterator(internal.iterator());
    }

    public UiObject elementWithText(String text) {
        return firstMatching(UiParts.withText(text));
    }

    public UiObject first() {
        return internal.first().get();
    }

    public UiObject firstMatching(Predicate<WebElement> filter) {
        Optional<UiObject> result = internal.firstMatch(filter);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new NoSuchElementException(String.format("Element identified by %s with filter %s not found", internal.first().get().getLocator(), filter));
        }
    }

    public ListOfUiObjects filter(Predicate<WebElement> filter) {
        return from(internal.filter(filter).toList());
    }

    public FluentIterable<UiObject> fluentIterable() {
        return internal;
    }

    public List<UiObject> list() {
        return internal.toList();
    }
}
