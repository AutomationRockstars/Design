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

package com.automationrockstars.gir.mobile;

import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

public class PageUtilsTest {

    @Test
    public void test() {
        System.out.println(Pattern.matches(".*Current.* ~ \\d.* .*", "<UIALink name=\"Current account ~ 0671 �2,828.95\" label=\"Current account ~ 0671 �2,828.95\" value=\"\" dom=\"[object Object]\"  visible=\"false\" hint=\"\" path=\"/0/0/3/0/2\" x=\"11\" y=\"129\" width=\"353\" height=\"56\">"));
        String pattern = "~ \\d* [\\w,\\s]*";
        String[] a = "Current account ~ 0671 �2,828.95".split(pattern);
        String[] b = "CDC SHAKEDOWN TEST ~ 3053 Credit Card �-41.02".split(pattern);
        System.out.println(Arrays.toString(a) + "\n" + Arrays.toString(b));
        System.out.println(Pattern.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d", "16/03/2016"));
    }

}
