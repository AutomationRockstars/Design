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

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.bmo.event.processor.internal.ProcessorFactory;
import com.automationrockstars.gunter.events.EventListener;
import com.automationrockstars.gunter.rabbit.Consumer;
import com.automationrockstars.gunter.rabbit.Publisher;
import com.automationrockstars.gunter.rabbit.RabbitEventBroker;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Paths;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RabbitEventBroker.class)
public class ServerTest {

    private static Consumer mockedConsumer = Mockito.mock(Consumer.class);
    private static Publisher mockedPublisher = Mockito.mock(Publisher.class);
    private static int freePort;

    @BeforeClass
    public static void prepareVariables() throws IOException {
        freePort = HttpContentTest.freePort();
        ConfigLoader.config().setProperty("server.pull", 10);
        ConfigLoader.config().setProperty("server.port", freePort);
        System.out.println(freePort);
    }

    @Before
    public void prepareMocks() {
        PowerMockito.mockStatic(RabbitEventBroker.class);
        Mockito.when(RabbitEventBroker.consumer(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedConsumer);
        Mockito.when(RabbitEventBroker.publisher(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedPublisher);
        Mockito.doNothing().when(mockedConsumer).registerListener(Mockito.any(EventListener.class));
    }

    @Test
    public void should_unloadAllRules() {
        assertThat(ProcessorFactory.processors().entrySet().size(), is(equalTo(0)));
        Server.start();
        assertThat(ProcessorFactory.processors().entrySet().size(), is(greaterThan(0)));
        Server.stop();

    }

    //	@Test
    public void should_startAndStopServer() throws IOException, InterruptedException {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Server.main(null);
                } catch (IOException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
        FileUtils.waitFor(Paths.get("server.lock").toFile(), 2);
        boolean opened = false;
        try {
            new Socket(InetAddress.getLoopbackAddress(), freePort).close();
            opened = true;
        } catch (Throwable d) {
            d.printStackTrace();
            opened = false;
        }
        assertThat("Web server is not started", opened);
        try {
            FileUtils.forceDelete(Paths.get("server.lock").toFile());
            Thread.sleep(11);
        } catch (IOException fileDeleteIssue) {
        }
        try {
            new Socket(InetAddress.getLoopbackAddress(), freePort).close();
            opened = true;
        } catch (ConnectException d) {
            opened = false;
        }
        assertThat("Web server is not stopped", !opened);

    }

}
