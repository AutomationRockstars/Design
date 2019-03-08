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

import com.automationrockstars.design.gir.webdriver.InitialPage;
import org.openqa.selenium.By.ByName;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;


@InitialPage(url = "http://google.com")
@Name("Google initial page")
public interface GoogleHome extends UiPart {

    @Find(@By(how = ByName.class, using = "q"))
    TextInput query();


    @Find(value = {
            @By(how = ByName.class, using = "btnK"),
            @By(how = ByName.class, using = "btnG")},
            any = true)
    Button search();


}
