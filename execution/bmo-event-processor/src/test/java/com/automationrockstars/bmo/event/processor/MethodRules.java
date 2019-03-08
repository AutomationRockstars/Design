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

package com.automationrockstars.bmo.event.processor;

import com.automationrockstars.bmo.event.processor.annotations.ExchangeReceiver;
import com.automationrockstars.bmo.event.processor.annotations.ExchangeSender;
import com.automationrockstars.bmo.event.processor.annotations.Rule;

public class MethodRules {

    @Rule("exchange to exchange")
    @ExchangeReceiver("input")
    @ExchangeSender("output")
    public static Message goodExchangeToExchange(Message testEvent) {
        return null;
    }


}
