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
package com.automationrockstars.gunter.events;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventStoreTest {

    @Test
    public void test() throws InterruptedException {

        final JobScheduled js = EventFactory.createJobScheduled("a", null);


        final TestExecutionStart es = EventFactory.createExecutionStart("asdasd");

        EventStore.putEvent(js);
        EventStore.putEvent(es);
        assertThat(EventStore.getEvent(JobScheduled.class), equalTo(js));
        assertThat(EventStore.getEvent(TestExecutionStart.class), equalTo(es));

        new Thread(new Runnable() {
            @Override
            public void run() {
                assertThat(EventStore.getEvent(JobScheduled.class), equalTo(js));
                assertThat(EventStore.getEvent(TestExecutionStart.class), equalTo(es));
                final JobScheduled childjs = EventFactory.createJobScheduled("b", null);
                final TestExecutionStart childes = EventFactory.createExecutionStart("asdasd");
                EventStore.putEvent(childjs);
                EventStore.putEvent(childes);
                assertThat(EventStore.getEvent(JobScheduled.class), equalTo(childjs));
                assertThat(EventStore.getEvent(TestExecutionStart.class), equalTo(childes));
            }
        }).start();
        Thread.sleep(200);
        assertThat(EventStore.getEvent(JobScheduled.class), equalTo(js));
        assertThat(EventStore.getEvent(TestExecutionStart.class), equalTo(es));
    }

}
