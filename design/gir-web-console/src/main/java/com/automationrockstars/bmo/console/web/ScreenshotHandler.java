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
package com.automationrockstars.bmo.console.web;

import com.automationrockstars.bmo.console.peer.RequestToNode;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.components.cipher.Base64;

import java.io.IOException;
import java.util.HashMap;

public class ScreenshotHandler implements HttpRequestHandler {

    private static Logger LOG = LoggerFactory.getLogger(ScreenshotHandler.class);

    public synchronized static String getScreenshotContent() throws ParseException, IOException {
        String content = RequestToNode.getScreenshot();
        content = new Gson().fromJson(content, HashMap.class).get("value").toString();
        LOG.debug("content {}", content);
        Preconditions.checkState(Base64.isArrayByteBase64(content.getBytes()), "Screenshot has wrong format");
        return content;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        byte[] content = null;
        ContentType type = ContentType.TEXT_PLAIN;
        try {
            if (URLEncodedUtils.parse(request.getRequestLine().getUri(), Charsets.UTF_8).size() == 1) {
                LOG.info("getting plain image");
                type = ContentType.create("image/png");
                content = Base64.decodeBase64(getScreenshotContent().getBytes());
            } else {
                LOG.info("getting base64 image");
                content = ("data:image/png;base64," + getScreenshotContent()).getBytes();
            }
            HttpEntity image = new ByteArrayEntity(content, type);
            response.setEntity(image);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setReasonPhrase("Screenshot not available due to " + e);
            LOG.error("Cannot provide screenshot", e);
        }
    }
}
