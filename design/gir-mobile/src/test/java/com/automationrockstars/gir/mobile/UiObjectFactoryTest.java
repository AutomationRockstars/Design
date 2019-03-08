/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gir.mobile;

import org.junit.Test;

public class UiObjectFactoryTest {

    @Test
    public void testFromString() {
        MobileUiObject a = MobileUiObjectFactory.from(" {  android: 'Loading...&&img sss sd', ios: 'Loading...'}  ");
        System.out.println(a.getLocators().get(MobileFactory.ANDROID));
    }

}
