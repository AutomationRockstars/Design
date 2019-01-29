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

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UiDelegateTest {

    @Test
    public void should_transforToWebdriverBy() throws InterruptedException, IOException {
        if (ConfigLoader.config().containsKey("noui")) {
//			int res = -1;
//			try {
//				res = new ProcessBuilder("phantomjs").start().waitFor(); 
//			} catch (Exception e){
//				res = -1;
//			}
//			if (res == 0){
//				ConfigLoader.config().setProperty("webdriver.browser", "phantomjs");
//			} else {
//				return ;
//			}
        }
        Method m = FluentIterable.
                from(Lists.newArrayList(GoogleHome.class.getMethods()))
                .firstMatch(new Predicate<Method>() {

                    public boolean apply(Method input) {
                        return input.getName().contains("query");
                    }
                }).get();
        assertThat(UiParts.buildBy(m), is(equalTo(org.openqa.selenium.By.name("q"))));
    }

}
