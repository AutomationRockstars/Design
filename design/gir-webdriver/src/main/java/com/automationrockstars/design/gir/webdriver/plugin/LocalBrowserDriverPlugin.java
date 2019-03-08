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
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static com.automationrockstars.base.ConfigLoader.config;


public class LocalBrowserDriverPlugin implements UiDriverPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(LocalBrowserDriverPlugin.class);
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
        } catch (IOException | InterruptedException e) {
            LOG.error("Cannot enable downloader", e);
        }
    }

    @Override
    public void beforeInstantiateDriver() {

        if (config().containsKey("webdriver.browser")
                && !config().containsKey("grid.url")) {
            final String browser = config().getString("webdriver.browser");
            String webDriverProperty = String.format("webdriver.%s.driver", browser);
            if (System.getProperty(webDriverProperty) == null) {
                if (config().getString(webDriverProperty) != null
                        && Paths.get(config().getString(webDriverProperty)).toFile().exists()) {
                    LOG.info("Setting local driver to {}",config().getString(webDriverProperty));
                    System.setProperty(webDriverProperty, config().getString(webDriverProperty));
                } else {
                    LOG.info("Downloading and setting local driver");
                    if (!FluentIterable.from(Lists.newArrayList(Paths.get(".").toFile().list())).firstMatch(input -> input.toLowerCase().contains(browser.toLowerCase())).isPresent()) {
                        downloadDrivers(browser);
                    }
                    if (! System.getProperty("os.name").toLowerCase().contains("win")){
                        LOG.info("Using driver manager to get latest version");
                        if (browser.contains("chrome")){
                            ChromeDriverManager.getInstance().setup();
                        }

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
