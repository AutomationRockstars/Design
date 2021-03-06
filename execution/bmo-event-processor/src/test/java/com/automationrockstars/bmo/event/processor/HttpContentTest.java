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

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.webbitserver.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;

public class HttpContentTest {

    public static String serverUrl;
    private static WebServer server;

    public static int freePort() throws IOException {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        }

    }

    @BeforeClass
    public static void startServer() throws IOException {

        try {
            int freePort = freePort();
            server = WebServers.createWebServer(freePort).add(new HttpHandler() {

                @Override
                public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
                    Map<String, Object> responseContent = Maps.newHashMap();
                    responseContent.put("content", request.body());
                    List<Entry<String, String>> headers = request.allHeaders();
                    Header[] properHeaders = new Header[headers.size()];
                    int i = 0;
                    for (Entry<String, String> header : headers) {
                        properHeaders[i++] = new BasicHeader(header.getKey(), header.getValue());
                    }
                    new Gson().toJson(properHeaders);
                    responseContent.put("headers", properHeaders);

                    String result = new Gson().toJson(responseContent);
                    response.content(result).end();

                }
            }).start().get();
            serverUrl = String.format("http://localhost:%s", freePort);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @AfterClass
    public static void stopServer() {
        try {
            server.stop().get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void should_returnPopulatePut() throws ClientProtocolException, IOException {
        String result = HttpContent.create("TEST MSG").header("TEST_HEADER", "PASSED").populate(Request.Put(serverUrl)).execute().returnContent().asString();
        ValidationResponse resultData = new Gson().fromJson(result, ValidationResponse.class);
        assertThat(resultData.getContent(), is(equalTo("TEST MSG")));
        assertThat(Arrays.toString((resultData.getHeaders())), containsString(new BasicHeader("TEST_HEADER", "PASSED").toString()));
    }

    @Test
    public void should_returnPopulatedPost() throws ClientProtocolException, IOException {
        String result = HttpContent.create(Collections.singletonMap("form", "value")).header("TEST_HEADER", "PASSED").populate(Request.Post(serverUrl)).execute().returnContent().asString();
        ValidationResponse resultData = new Gson().fromJson(result, ValidationResponse.class);
        assertThat(resultData.getContent(), is(equalTo("form=value")));
        assertThat(Arrays.toString((resultData.getHeaders())), containsString(new BasicHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString()).toString()));
    }

    public class ValidationResponse {
        String content;
        BasicHeader[] headers;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public BasicHeader[] getHeaders() {
            return headers;
        }

        public void setHeaders(BasicHeader[] headers) {
            this.headers = headers;
        }

    }

}
