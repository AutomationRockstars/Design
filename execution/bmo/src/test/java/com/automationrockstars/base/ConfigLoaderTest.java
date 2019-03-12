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
package com.automationrockstars.base;

import com.automationrockstars.base.internal.ContextConfiguration;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

public class ConfigLoaderTest {

    @Test
    public void getConfig() {
        assertThat(ConfigLoader.config(), is(notNullValue()));
    }

    @Test
    public void should_readFromSystem() {
        System.setProperty("t1", "1");
        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("1")));
        ConfigLoader.config().setProperty("t1", "2");
        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("2")));
    }

    public static class SuperLame extends Thread{

        public SuperLame(){
            super();
            super.setName("SDFSFS");
        }
        public void run(){
            System.out.println("sl" + Thread.currentThread());
        }



    }
    @Test
    public void should_separateContexts() throws Exception{
        System.setProperty("t1", "1");
        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("1")));
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("1")));
            executorService.submit(() -> {
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("1")));
                ConfigLoader.addContextProperty("t1","7");
                assertThat(ContextConfiguration.get().getString("t1"), is(equalTo("7")));
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("7")));
                return ConfigLoader.config().getString("t1");
            }).get();


            executorService.submit(() -> {
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("1")));
                assertThat(ConfigLoader.config().getString("t1"), is(not((equalTo("7")))));
                ConfigLoader.addContextProperty("t1","4");
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("4")));
                ConfigLoader.setContextProperty("t1","5");
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("5")));
                ConfigLoader.config().setProperty("t1","6");;
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("5")));
                ConfigLoader.clearContextProperty("t1");
                assertThat(ConfigLoader.config().getString("t1"), is(equalTo("6")));
                return ConfigLoader.config().getString("t1");
            }).get();

        assertThat(ConfigLoader.config().getString("t1"), is(equalTo("6")));
    }
}
