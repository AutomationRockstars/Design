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

package com.automationrockstars.gir.data.converter;

import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.commons.lang.WordUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class NullConverter extends AbstractConverter {

    @SuppressWarnings("unchecked")
    public static <T> T nullConvert(Class<T> type) {

        if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
            return (T) Boolean.FALSE;
        } else if (type.equals(String.class)) {
            return type.cast("");
        } else if (Number.class.isAssignableFrom(type)) {
            try {
                return type.getConstructor(String.class).newInstance("0");
            } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                return null;
            }
        } else if (type.equals(Date.class)) {
            return type.cast(new Date());
        } else if (type.isPrimitive()) {
            try {
                String name = type.getSimpleName();
                if (name.equals("int")) {
                    name = "Integer";
                }
                return (T) Class.forName("java.lang." + WordUtils.capitalize(name)).getConstructor(String.class).newInstance("0");
            } catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        return nullConvert(type);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Void.class;

    }

}
