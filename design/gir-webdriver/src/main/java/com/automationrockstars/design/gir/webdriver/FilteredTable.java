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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.element.Table;

import java.util.List;
import java.util.function.Function;

public class FilteredTable extends Table {

    private static final Predicate<List<?>> notEmptyList = new Predicate<List<?>>() {

        @Override
        public boolean apply(List<?> input) {
            return input.size() > 0;
        }
    };

    public FilteredTable(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public List<List<WebElement>> getRows() {
        return Lists.newArrayList(Iterables.filter(super.getRows(), notEmptyList));
    }

    public Row row(int rowNo) {
        return new Row(this.getWrappedElement().findElements(By.tagName("tr")).get(rowNo));
    }

    public List<Row> rows() {
        return Lists.newArrayList(Iterables.transform(this.getWrappedElement().findElements(By.tagName("tr")), new com.google.common.base.Function<WebElement, Row>() {
            @Override
            public Row apply(WebElement input) {
                return new Row(input);
            }
        }));
    }

    public Row rowContainingText(final String rowText) {
        try {
            return Iterables.tryFind(rows(), new Predicate<Row>() {

                @Override
                public boolean apply(Row input) {
                    return input.hasText(rowText);
                }
            }).get();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Row " + rowText + " cannot be found");
        }
    }

    private boolean canClickRow(int no) {
        try {
            DriverFactory.actions().click(rows().get(no)).perform();
            return true;
        } catch (Throwable ignore) {
            return false;
        }
    }

    public void waitForSize(final int minimumSize) {
        Waits.withDelay(20).until(new Function<WebDriver, Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return rows().size() > minimumSize &&
                        canClickRow(minimumSize);

            }
        });
    }

}
