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

package com.automationrockstars.gir.data.converter;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.exparity.hamcrest.date.DateMatchers.*;
import static org.hamcrest.Matchers.is;

public class SmartDateConverterTest {

    @Test
    public void should_returnTodaysDate() {
        assertThat(new SmartDateConverter().convert(Date.class, "today"), isToday());
        assertThat(new SmartDateConverter().convert(Date.class, "now"), isToday());
    }

    @Test
    public void should_returnTomorrowDate() {
        assertThat(new SmartDateConverter().convert(Date.class, "tomorrow"), isTomorrow());
        assertThat(new SmartDateConverter().convert(Date.class, "in 1 day"), isTomorrow());
    }

    @Test
    public void should_returnDateIn2Months() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 20);
        assertThat(new SmartDateConverter().convert(Date.class, "in 20 days"), is(sameDay(c.getTime())));
        c = Calendar.getInstance();
        c.add(Calendar.MONTH, 2);
        assertThat(new SmartDateConverter().convert(Date.class, "in 2 months"), is(sameMonth(c.getTime())));
    }


}
