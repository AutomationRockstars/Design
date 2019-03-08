/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gir.mobile;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;
import java.util.regex.Pattern;

public class PageUtils {

    public static String getValueFromLine(String name, String pageLine) {
        return pageLine.replaceAll("^.* " + name + "=\"", "").replaceAll("\".*", "");
    }

    public static String getPageSource() {
        return MobileFactory.getDriver().getPageSource();
    }

    public static List<String> getPageSourceAsList() {
        return Splitter.on("\n").omitEmptyStrings().splitToList(getPageSource().replaceAll("><", ">\n<"));
    }

    private static Predicate<String> linesContaining(final String... parts) {

        return new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                boolean result = true;
                for (String part : parts) {
                    result = result && input.contains(part);
                }
                return result;
            }
        };
    }

    public static List<String> getLinesContaining(final String... parts) {
        return getLinesContaining(getPageSourceAsList(), parts);
    }

    static List<String> getLinesContaining(final Iterable<String> lines, final String... parts) {
        return Lists.newArrayList(Iterables.filter(lines, linesContaining(parts)));
    }

    static List<String> getLinesMatching(Iterable<String> lines, final String regex) {
        return Lists.newArrayList(Iterables.filter(lines, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return Pattern.matches(regex, input);
            }
        }));
    }

    public static List<String> getLinesMatching(final String regex) {
        return getLinesMatching(getPageSourceAsList(), regex);
    }

    public static List<String> getLinesBetween(String[] start, String[] end) {
        List<String> source = getPageSourceAsList();
        int startLine = 0;
        int endLine = source.size() - 1;
        if (!ArrayUtils.isEmpty(start)) {
            startLine = source.indexOf(getLinesContaining(source, start).get(0));
        }
        if (!ArrayUtils.isEmpty(end)) {
            endLine = source.indexOf(getLinesContaining(source, end).get(0));
            if (endLine < 0) {
                endLine = source.size() - 1;
            }
        }
        return source.subList(startLine + 1, endLine - 1);
    }

    public static List<String> getLinesBetween(String start, String end) {
        List<String> source = getPageSourceAsList();
        int startLine = source.indexOf(getLinesContaining(start).get(0));
        int endLine = source.indexOf(getLinesContaining(end).get(0));
        return source.subList(startLine + 1, endLine - 1);
    }

    public static List<String> getNameValues(List<String> linesBetween) {
        return Lists.newArrayList(
                Iterables.transform(Iterables.filter(linesBetween, linesContaining("name")),
                        new Function<String, String>() {

                            @Override
                            public String apply(String input) {
                                return getValueFromLine("name", input);
                            }
                        }));
    }


}
