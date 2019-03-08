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

package com.automationrockstars.bmo;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

public class ArtefactsStore {

    private static final String destinationFolder = ConfigLoader.config().getString("report.target.data", "target/allure-report/data");
    private static final Logger LOG = LoggerFactory.getLogger(ArtefactsStore.class);
    private static Map<String, String> stored = Maps.newConcurrentMap();

    public static synchronized void store(String name, String webTag) {
        stored.put(name, webTag);
    }

    private static void copyFile(File originalLocation) {
        try {
            FileUtils.copyFile(originalLocation, Paths.get(destinationFolder, originalLocation.getName()).toFile());
        } catch (IOException e) {
            LOG.error("Inculding file {} as publishable artifact failed due to {}", originalLocation, e.getMessage());
        }
    }

    public static void storeTextFile(String name, File text) {
        copyFile(text);
        store(name, String.format("<a href=\"%s/%s\">"
                        + "<object id=\"%s\" width=\"160\" height=\"88\" type=\"text/plain\">" +
                        "Your browser does not support HTML5 video.</object></a>",
                destinationFolder, text.getName(), name, destinationFolder, text.getName()));
    }

    public static void storeImage(String name, File image) {
        copyFile(image);
        store(name, String.format("<img src=\"%s/%s\">", destinationFolder, image.getName()));
    }

    public static void storeLink(String name, String url) {
        store(name, String.format("<a href=\"%s\">%s</a>", url, name));
    }

    public static void storeVideo(String name, Callable<File> video) {
        try {
            storeVideo(name, video.call());
        } catch (Exception e) {
            LOG.error("Fetching file with {} failed due to {}", video, e);
        }
    }

    public static void storeVideo(String name, File video) {
        copyFile(video);
        store(name, String.format("<a href=\"%s/%s\"><video id=\"%s\" width=\"160\" height=\"88\" autoplay=\"true\" controls=\"true\">" +
                "<source src=\"%s/%s\" type=\"video/mp4\">" +
                "Your browser does not support HTML5 video.</video></a>", destinationFolder, video.getName(), name, destinationFolder, video.getName()));
    }

    public static void storeText(String name, String text) {
        store(name, String.format("<span>%s</span>", text));
    }

    public static Properties populate(Properties initial) {
        for (Map.Entry<String, String> prop : stored.entrySet()) {
            initial.setProperty(prop.getKey(), prop.getValue());
        }
        return initial;
    }

}
