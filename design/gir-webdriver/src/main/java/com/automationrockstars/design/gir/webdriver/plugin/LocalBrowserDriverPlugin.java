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

package com.automationrockstars.design.gir.webdriver.plugin;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class LocalBrowserDriverPlugin implements UiDriverPlugin {

    public static synchronized void downloadDrivers(String browserName) {
        try {
            Files.write(Paths.get("Downloader.java"), IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("downloader")), StandardOpenOption.CREATE);
            try {
                Process compile = new ProcessBuilder("javac", "Downloader.java").inheritIO().start();
                compile.waitFor();
            } catch (Throwable e) {
            }
            Process download = new ProcessBuilder("java", "-Djava.net.useSystemProxies=true", "-Dbrowser=" + browserName, "Downloader").inheritIO().start();
            download.waitFor();
            FileUtils.forceDelete(Paths.get("Downloader.java").toFile());
            FileUtils.forceDelete(Paths.get("Downloader.class").toFile());
            FileUtils.forceDelete(Paths.get("Downloader$1.class").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeInstantiateDriver() {
        if (ConfigLoader.config().containsKey("webdriver.browser")
                && !ConfigLoader.config().containsKey("grid.url")) {
            final String browser = ConfigLoader.config().getString("webdriver.browser");
            String webDriverProperty = String.format("webdriver.%s.driver", browser);
            if (System.getProperty(webDriverProperty) == null) {
                if (ConfigLoader.config().getString(webDriverProperty) != null
                        && Paths.get(ConfigLoader.config().getString(webDriverProperty)).toFile().exists()) {
                    System.setProperty(webDriverProperty, ConfigLoader.config().getString(webDriverProperty));
                } else {
                    if (!FluentIterable.from(Lists.newArrayList(Paths.get(".").toFile().list())).firstMatch(new Predicate<String>() {

                        @Override
                        public boolean apply(String input) {
                            return input.toLowerCase().contains(browser.toLowerCase());
                        }
                    }).isPresent()) {
                        downloadDrivers(browser);
                    }
                }
            }
        }

        try {
            Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".exe")) {
                        if (file.toString().contains("chrome")) {
                            System.setProperty("webdriver.chrome.driver", file.toFile().getAbsolutePath());
                        } else if (file.toString().contains("IE")) {
                            System.setProperty("webdriver.ie.driver", file.toFile().getAbsolutePath());
                        } else if (file.toString().contains("phantomjs")) {
                            System.setProperty("webdriver.phantomjs.driver", file.toFile().getAbsolutePath());
                        } else if (file.toString().contains("gecko")) {
                            System.setProperty("webdriver.geckodriver.driver", file.toFile().getAbsolutePath());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

    @Override
    public void afterGetDriver(WebDriver driver) {

    }

    @Override
    public void beforeCloseDriver(WebDriver driver) {

    }

    @Override
    public void afterCloseDriver() {


    }

    @Override
    public void beforeGetDriver() {
    }

    @Override
    public void afterInstantiateDriver(WebDriver driver) {
    }


}
