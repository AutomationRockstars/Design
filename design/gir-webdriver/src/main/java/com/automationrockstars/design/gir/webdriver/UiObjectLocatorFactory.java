/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.design.gir.webdriver;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementClassAnnotationsHandler;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementFieldAnnotationsHandler;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.lang.reflect.Field;

public class UiObjectLocatorFactory extends HtmlElementLocatorFactory {

    SearchContext searchContext;

    public UiObjectLocatorFactory(SearchContext searchContext) {
        super(searchContext);
        this.searchContext = searchContext;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        return new NamedElementLocator(searchContext, getTimeOut(field), new HtmlElementFieldAnnotationsHandler(field)).withField(field);
    }


    public ElementLocator createLocator(Class clazz) {
        return new NamedElementLocator(searchContext, getTimeOut(clazz), new HtmlElementClassAnnotationsHandler(clazz)).withClass(clazz);
    }

}
