package com.automationrockstars.customer.hw.fluentIterable;

import org.openqa.selenium.By;
import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.FindByAugmenter;
import com.automationrockstars.gir.ui.UiPart;
import com.google.common.base.Preconditions;

import static com.automationrockstars.gir.ui.part.FindByAugmenters.instanceForValue;
import static com.automationrockstars.gir.ui.part.FindByAugmenters.using;

public class PropertyAugmenter implements FindByAugmenter {

    private static Integer propertyId = null;

    @Override
    public By augment(Class<? extends UiPart> parent, FindBy toBeAugmented) {
        Preconditions.checkNotNull(propertyId);
        By by = instanceForValue(toBeAugmented, using(toBeAugmented).replace("propertyId", String.valueOf(propertyId)));
        return by;
    }

    public static void setPropertyId(Integer propertyId) {
        PropertyAugmenter.propertyId = propertyId;
    }
}
