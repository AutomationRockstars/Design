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
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;
import ru.yandex.qatools.htmlelements.pagefactory.CustomElementLocatorFactory;

public class UiObjectDecorator extends HtmlElementDecorator {

    public UiObjectDecorator(CustomElementLocatorFactory locatorFactory) {
        super(locatorFactory);
    }

    public UiObjectDecorator(SearchContext driver) {
        super(new HtmlElementLocatorFactory(driver));
    }

    public UiObjectDecorator() {
        this(DriverFactory.getDriver());
    }

}
