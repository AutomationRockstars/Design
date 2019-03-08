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

import com.automationrockstars.gunter.events.Event;

import static com.automationrockstars.bmo.event.processor.Message.Builder.newMessage;

public interface Action<T extends Event> {

    public static final Action<Event> DO_NOTHING = new Action<Event>() {

        @Override
        public Message process(Event event) {
            return null;
        }
    };
    public static final Action<Event> PASS_TROUGH = new Action<Event>() {

        @Override
        public Message process(Event event) {
            return newMessage().withEvent(event);
        }

    };
    public static final Action<Event> STORE = new Action<Event>() {

        @Override
        public Message process(Event event) {
            EventStorage.storage().storeIfParentStored(event);
            return null;
        }
    };

    Message process(T event);
}
