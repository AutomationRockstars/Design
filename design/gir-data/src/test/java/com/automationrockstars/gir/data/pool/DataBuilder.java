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

package com.automationrockstars.gir.data.pool;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataPool;
import com.automationrockstars.gir.data.TestDataServices;
import com.automationrockstars.gir.data.impl.TestDataPermutatorImpl;
import com.google.common.base.Predicate;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

public class DataBuilder {

    public static TestData<Booking> buildBooking() throws IOException {
        TestDataPool bookingPool = TestDataServices.pool("booking");
        bookingPool.loadFrom("currency.json", "credit.json", "cancellation_protection.json", "affiliate_payback.json");

        TestDataPool.TestDataPermutator bookings = new TestDataPermutatorImpl(bookingPool)
                .combine(Currency.class, Credit.class, CancellationProtection.class, AffiliatePayback.class)
                .exclude((Predicate<Booking>) booking -> exclusions(booking));

        TestData build = bookings.build(Booking.class);
        return build;
    }

    private static boolean exclusions(Booking booking) {
        return (booking.affiliatePayback().percentage() && !booking.affiliatePayback().flat())
                && !booking.credit().deposit()
                && (booking.credit().promo() || booking.credit().cancellationProtection() || booking.credit().bookingGuarantee());
    }

    @Test
    public void dataPermutations() throws IOException {
        TestData<Booking> bookings = buildBooking();

        assertThat(bookings.records().size(), not(54));
        assertThat(bookings.records().size(), is(46));
        assertThat("Records matching exclusion returned", !bookings.records().anyMatch(booking -> exclusions(booking)));
    }
}