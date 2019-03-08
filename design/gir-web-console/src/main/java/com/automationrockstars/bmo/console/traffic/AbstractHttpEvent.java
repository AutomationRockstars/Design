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
package com.automationrockstars.bmo.console.traffic;

import com.google.common.collect.Lists;
import org.apache.http.Header;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

public abstract class AbstractHttpEvent {

    private byte[] content;
    private List<Header> headers;

    public BufferedInputStream getContent() {
        if (content != null) {
            return new BufferedInputStream(new ByteArrayInputStream(content));
        } else return null;
    }

    public void setContent(BufferedInputStream content) {
        this.content = HttpEventUtils.copyToBytes(content);
    }

    public byte[] readContent() {
        return content;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = Lists.newArrayList(headers);
    }

}
