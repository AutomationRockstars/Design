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

package com.automationrockstars.gir.ui.part;

import com.google.common.collect.Queues;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricFileWriter {

    private static ConcurrentLinkedQueue<String> lines = Queues.newConcurrentLinkedQueue();
    private static final Runnable write = new Runnable() {

        @Override
        public void run() {
            while (lines.size() > 0) {
                writeLine();
            }
        }
    };
    private static ScheduledExecutorService writer = Executors.newSingleThreadScheduledExecutor();

    static {
        writer.scheduleAtFixedRate(write, 5, 2, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                writer.shutdownNow();
                try {
                    writer.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {

                } finally {
                    write.run();
                }

            }
        }));
    }

    private static void writeLine() {
        try {
            String line = String.format("%s\n", lines.poll());
            if (line != null && line.length() > 6 && line.getBytes() != null && line.getBytes().length > 6) {
                Files.write(Paths.get("metric.output"), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void put(String time, String host, String method, String har) {
        lines.add(String.format("%s,%s,%s,%s", time, host, method, har));
    }


}
