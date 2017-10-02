package com.automationrockstars.gir.data.pool;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataPool;
import com.automationrockstars.gir.data.TestDataServices;
import com.automationrockstars.gir.data.impl.TestDataPermutatorImpl;

import java.io.IOException;

public class DataBuilder {

    public static void main(String[] args) throws IOException {
        TestData<Booking> booking = buildBooking();

        System.out.println();
    }

    public static TestData<Booking> buildBooking() throws IOException {
        loadData();
        TestData<Currency> currencies = TestDataServices.pool("currency").testData(Currency.class);
        TestData<Credit> credits = TestDataServices.pool("credit").testData(Credit.class);
        TestData<CancellationProtection> cancellationProtection = TestDataServices.pool("cancellation_protection").testData(CancellationProtection.class);
        TestData<AffiliatePayback> affiliatePayback = TestDataServices.pool("affiliate_payback").testData(AffiliatePayback.class);

        TestDataPool bookingPool = TestDataServices.pool("booking");
        bookingPool.loadFrom("currency.json");
        bookingPool.loadFrom("credit.json");
        bookingPool.loadFrom("cancellation_protection.json");
        bookingPool.loadFrom("affiliate_payback.json");

        TestDataPool.TestDataPermutator bookings = new TestDataPermutatorImpl(bookingPool).combine(Currency.class, Credit.class, CancellationProtection.class, AffiliatePayback.class);

        TestData build = bookings.build(Booking.class);
        return build;
    }

    private static void loadData() {
        TestDataServices.pool("currency").loadFrom("currency.json");
        TestDataServices.pool("credit").loadFrom("credit.json");
        TestDataServices.pool("cancellation_protection").loadFrom("cancellation_protection.json");
        TestDataServices.pool("affiliate_payback").loadFrom("affiliate_payback.json");
    }
}