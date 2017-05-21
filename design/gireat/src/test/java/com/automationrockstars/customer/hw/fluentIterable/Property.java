package com.automationrockstars.customer.hw.fluentIterable;

import com.automationrockstars.gir.ui.Filter;
import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.UiPart;
import org.openqa.selenium.WebElement;

@FindBy(className = "hwta-property")
public interface Property extends UiPart {

    @FindBy(className = "proptype")
    WebElement propertyType();

    @FindBy(tagName = "h2")
    WebElement propertyName();

    @FindBy(className = "addressline")
    WebElement address();

    Rating ratingDetails();

    @FindBy(className = "icon_freewifi")
    WebElement freeWifi();

    @FindBy(className = "icon_freebrekkie")
    WebElement freeBreakfast();

    @FindBy(className = "mobileprice")
    WebElement mobilePrice();

    @FindBy(tagName = "p")
    WebElement description();

    @FindBy(className = "hwta-property-link")
    @Filter("text.contains('More...')")
    WebElement moreInformation();

    @FindBy(css = "compare.a")
    WebElement compare();

    @FindBy(className = "moreinfo")
    WebElement viewProperty();

    @FindBy(className = "hw-recommends")
    WebElement hostelworldRecommends();

    @FindBy(className = "rounded active")
    WebElement displayedImage();

}
