package com.automationrockstars.customer.hw.fluentIterable;

import static com.automationrockstars.asserts.Asserts.assertThat;
//import static org.hamcrest.Matchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.ByChained;

import com.automationrockstars.design.gir.webdriver.ByOrder;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.automationrockstars.gir.ui.UiParts;
import com.google.common.collect.FluentIterable;

public class FluentIterableTest {

    @Test
    public void getRatingTest() {
        DriverFactory.getDriver().get("http://www.hostelworld.com/findabed.php/ChosenCity.Belgrade/ChosenCountry.Serbia?updatecache=1&ShowAll=1");
        FluentIterable<Property> properties = UiParts.on(PropertyList.class).properties();
        String first = properties.get(0).ratingDetails().reviewCount().getText();
        String second = properties.get(1).ratingDetails().reviewCount().getText();
        String third = properties.get(2).ratingDetails().reviewCount().getText();

        assertFalse(first.equals(second) && first.equals(third) && third.equals(second));
        
        PropertyAugmenter.setPropertyId(35704);
        assertThat(Integer.valueOf(UiParts.on(PropertyList.class).property().ratingDetails().reviewCount().getText()),greaterThanOrEqualTo(Integer.valueOf(906)));
    }
}
