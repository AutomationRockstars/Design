package com.automationrockstars.customer.hw.fluentIterable;

import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.UiPart;
import com.google.common.collect.FluentIterable;


public interface PropertyList extends UiPart {

    FluentIterable<Property> properties();

    SingleProperty property();

}
