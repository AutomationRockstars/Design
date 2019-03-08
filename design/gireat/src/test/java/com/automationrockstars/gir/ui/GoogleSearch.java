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

package com.automationrockstars.gir.ui;

import static com.automationrockstars.gir.ui.UiParts.get;
import static com.automationrockstars.gir.ui.UiParts.on;

public class GoogleSearch {


    public static SearchResults performSearch(String query) {
        on(GoogleHome.class).query().clear();
        on(GoogleHome.class).query().sendKeys(query);
        on(GoogleHome.class).search().click();
        return get(SearchResults.class);
    }
}
