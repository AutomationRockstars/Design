package com.automationrockstars.gir.data.pool;

import com.automationrockstars.gir.data.TestDataRecord;

public interface Credit extends TestDataRecord {

    Boolean promo();
    Boolean cancellationProtection();
    Boolean bookingGuarantee();
    Boolean deposit();
}
