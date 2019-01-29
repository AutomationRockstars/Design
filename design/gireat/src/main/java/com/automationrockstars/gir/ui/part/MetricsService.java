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

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.CharMatcher;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.openqa.selenium.logging.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class MetricsService {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsService.class);
    private static Gson gson = new Gson();

    public static void finished(Object host, Method method, Object[] args, long elapsed) {
        LOG.info("Execution of {} took {} nanos", method.getName(), elapsed);

        if (ConfigLoader.config().getBoolean("performance.run", false) && DriverFactory.isPhantom()) {
            logNow(host.toString(), method.getName(), "");
        }

    }

    public static void logNow(String host, String method, String... pageFilter) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM:dd-HH:mm:ss.SSS");
        if (DriverFactory.isPhantom()) {
            for (LogEntry log : DriverFactory.getDriver().manage().logs().get("har").getAll()) {
                Map<String, String> timings = timings(log.getMessage(), pageFilter);
                MetricFileWriter.put(sdf.format(new Date(log.getTimestamp())), host, method, pageAndTime(timings));
                MetricsRabbitWriter.log(host, method, data(timings));
            }
        }
    }

    public static synchronized String pageAndTime(Map<String, String> timings) {
        String result = "";
        for (String page : timings.keySet()) {
            result = String.format("%s, %s, %s", result, page, timings.get(page));
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private synchronized static Map<String, String> timings(String har, String... pageFilter) {
        Map content = gson.fromJson(har, HashMap.class);

        List<Map> entries = (List<Map>) ((Map) content.get("log")).get("entries");
        Map<String, String> result = Maps.newHashMap();
        for (Map entry : entries) {
            if (entry.containsKey("pageref")) {
                boolean match = false;
                for (String filter : pageFilter) {
                    match = match || entry.get("pageref").toString().contains(filter);
                }
                if (match) {
                    String pageRef = entry.get("pageref").toString();
                    int i = 0;
                    while (result.containsKey(pageRef)) {
                        pageRef += i++;
                    }
                    result.put(pageRef, entry.get("timings").toString());
                }
            }
        }
        return result;
    }

    public static synchronized Map<String, Number> data(Map<String, String> timings) {
        Long timeStamp = System.currentTimeMillis();
        Map<String, Number> result = Maps.newHashMap();
        try {

            String timingsString = FluentIterable.from(timings.entrySet()).toSortedList(new Comparator<Map.Entry<String, String>>() {

                @Override
                public int compare(Entry<String, String> arg0, Entry<String, String> arg1) {
                    return arg0.getKey().length() - arg1.getKey().length();
                }
            }).iterator().next().getValue();
            if (timingsString != null) {
                String[] times = CharMatcher.JAVA_DIGIT.or(CharMatcher.anyOf(",.")).retainFrom(timingsString).split(",");
                Float val = 0F;
                for (String time : times) {
                    val += Float.valueOf(time);
                }
                result.put("value", Math.round(val));
                result.put("timeStamp", timeStamp * 1000000);
            }

        } catch (Exception e) {
            LOG.debug("Error with data {}", e.getMessage());
        }
        return result;
    }


}
