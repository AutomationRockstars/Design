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
package com.automationrockstars.bmo;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.jbehave.core.steps.ParameterConverters.ParameterConverter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapParameterConverter implements ParameterConverter {

    @Override
    public boolean accept(Type type) {
        return type.toString().contains(Map.class.getCanonicalName()) ||
                type.toString().contains(HashMap.class.getCanonicalName());

    }

    @Override
    public Object convertValue(String value, Type type) {
        Map<String, String> result = Maps.newHashMap();
        List<String> entries = Splitter.on(",").trimResults().splitToList(value);
        for (String entry : entries) {
            List<String> pair = Splitter.on(":").trimResults().splitToList(entry);
            result.put(pair.get(0), pair.get(1));
        }
        return result;
    }

}
