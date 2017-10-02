package com.automationrockstars.gir.data.pool;

import com.automationrockstars.gir.data.TestDataRecord;

public interface Booking extends TestDataRecord {

    Currency currency();
    Credit credit();
    CancellationProtection cancellationProtection();
    AffiliatePayback affiliatePayback();
}
