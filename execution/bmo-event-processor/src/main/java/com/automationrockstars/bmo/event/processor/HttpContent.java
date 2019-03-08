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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class HttpContent {

    private Map<String, String> headers = Maps.newHashMap();
    private Object content;
    private Class<?> contentClass;
    private ContentType contentType = ContentType.TEXT_PLAIN;

    public static HttpContent create() {
        return new HttpContent();
    }

    public static HttpContent create(Object content) {
        return new HttpContent().content(content);
    }

    public static HttpContent create(Object content, String encoding) {
        return new HttpContent().content(content).setEncoding(encoding);
    }

    public Map<String, String> headers() {
        return headers;
    }

    public HttpContent headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public HttpContent header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public HttpContent withContentType(String type) {
        this.contentType = ContentType.create(type);
        return this;
    }

    public HttpContent withContentType(ContentType type) {
        this.contentType = type;
        return this;
    }

    public HttpContent content(Object content) {
        this.content = content;
        this.contentClass = content.getClass();
        return this;
    }

    public HttpContent setEncoding(String encoding) {
        contentType = ContentType.create(encoding);
        return this;
    }

    public Class<?> getContentType() {
        return contentClass;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContent() {
        return (T) content;
    }

    @SuppressWarnings("unchecked")
    Request populate(Request toPopulate) {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            toPopulate.addHeader(header.getKey(), header.getValue());
        }
        if (content == null) return toPopulate;
        if (contentClass.equals(byte[].class)) {
            toPopulate.bodyByteArray((byte[]) content);
        } else if (contentClass.equals(String.class)) {
            toPopulate.bodyString((String) content, contentType);
        } else if (Map.class.isAssignableFrom(contentClass)) {
            List<NameValuePair> formContent = Lists.newArrayList();
            for (Map.Entry<String, String> val : ((Map<String, String>) content).entrySet()) {
                formContent.add(new BasicNameValuePair(val.getKey(), val.getValue()));
            }
            toPopulate.bodyForm(formContent);
        } else if (contentClass.equals(File.class)) {
            toPopulate.bodyFile((File) content, contentType);
        } else if (InputStream.class.isAssignableFrom(contentClass)) {
            toPopulate.bodyStream((InputStream) content);
        } else {
            throw new IllegalArgumentException(String.format("Unknown content class %s. Only byte[], String, Map, File and InputStream are accepted", contentClass));
        }
        return toPopulate;
    }

    public String toString() {
        return String.format("HttpContent type %s with headers %s content %s", contentType, headers, content);
    }

}
