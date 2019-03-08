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

package com.automationrockstars.bmo;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class DownloadFromSeleniumGrid implements Callable<File> {

    private static CloseableHttpClient cl;
    private final String link;

    public DownloadFromSeleniumGrid(String link) {
        this.link = link;
    }

    private static boolean canGetVideo(final String link) {
        CloseableHttpResponse resp = null;
        try {
            resp = cl.execute(new HttpGet(link));
            if (resp.getStatusLine().getStatusCode() != 200) {
                throw new IllegalArgumentException("Negative response from server " + resp.getStatusLine());
            }
            return true;
        } catch (Throwable t) {
            //LOG.debug("Video {} cannot be fetched due to {}",link,t.getMessage());
            return false;
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    CloseableHttpClient client() {
        return cl;
    }

    @Override
    public File call() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
