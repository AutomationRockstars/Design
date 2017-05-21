package com.automationrockstars.customer.hw.fluentIterable;

import com.automationrockstars.gir.ui.FindBy;
import com.automationrockstars.gir.ui.UiPart;
import org.openqa.selenium.WebElement;

@FindBy(className = "fabresult-details-rating")
public interface Rating extends UiPart {

    @FindBy(className = "ratingbox")
    WebElement vlaue();

    @FindBy(className = "ratingword")
    WebElement description();

    @FindBy(className = "fabresult-reviews-count")
    WebElement reviewCount();
}
