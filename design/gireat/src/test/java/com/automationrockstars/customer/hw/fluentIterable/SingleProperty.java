package com.automationrockstars.customer.hw.fluentIterable;

import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.WithFindByAugmenter;

@WithFindByAugmenter(PropertyAugmenter.class)
@FindBy(id = "searchResults_propertyId")
public interface SingleProperty extends Property {
}
