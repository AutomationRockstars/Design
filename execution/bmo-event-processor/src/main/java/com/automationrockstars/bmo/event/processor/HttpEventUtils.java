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

import com.automationrockstars.bmo.event.processor.annotations.HttpSender;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class HttpEventUtils {


    public static class RequestProducer {
        private static final Logger LOG = LoggerFactory.getLogger(RequestProducer.class);
        private final String type;
        private final String uri;
        private final String auth;
        private final String encoding;

        private RequestProducer(String type, String uri, String encoding, String auth) {
            this.type = type;
            this.uri = uri;
            this.auth = auth;
            this.encoding = encoding;
        }

        public static RequestProducer create(String type, String uri) {
            return new RequestProducer(type, uri, ContentType.TEXT_PLAIN.getMimeType(), null);
        }

        public static RequestProducer create(String type, String uri, String encoding, String auth) {
            return new RequestProducer(type, uri, encoding, auth);
        }

        public static RequestProducer create(String type, String uri, String encoding) {
            return new RequestProducer(type, uri, encoding, null);
        }

        public static RequestProducer create(HttpSender senderInfo) {
            return create(senderInfo.type(), senderInfo.url(), senderInfo.encoding(), senderInfo.auth());
        }

        public Request produce(HttpContent entity) {
            entity.setEncoding(encoding);
            Request result = null;

            try {
                result = (Request) FluentIterable.from(Lists.newArrayList(Request.class.getMethods())).firstMatch(new Predicate<Method>() {

                    @Override
                    public boolean apply(Method input) {
                        try {
                            return input.getName().toLowerCase().contains(type.toLowerCase())
                                    && input.getParameterTypes()[0].equals(String.class)
                                    && Modifier.isStatic(input.getModifiers());
                        } catch (Throwable ignore) {
                            LOG.trace("Error looking for method", ignore);
                            return false;
                        }
                    }
                }).get().invoke(null, uri);
                entity.populate(result);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | SecurityException e) {
                LOG.error("Cannot produce request for {}", e);
            }
            LOG.debug("Request {} {} created {}", type, uri, result);
            return result;
        }


        public Executor executor() {
            Executor exec = Executor.newInstance();
            if (!Strings.isNullOrEmpty(auth)) {
                exec.auth(auth.split(":")[0], auth.split(":")[1]);
            }
            return exec;

        }

        public Response execute(HttpContent content) throws ClientProtocolException, IOException {
            return executor().execute(produce(content));
        }

        public String toString() {
            return String.format("RequestProducer type %s for %s with encoding %s and auth [%s]", type, uri, encoding, auth);
        }
    }


}
