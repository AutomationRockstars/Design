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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.http.Header;
import org.apache.http.StatusLine;

import java.util.List;

public class HttpResponseEvent extends AbstractHttpEvent {

    private List<Header> headers;
    private StatusLine statusLine;


    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = Lists.newArrayList(headers);
    }


    public String toString() {
        return String.format("%s\n%s\n%s",
                getStatusLine(),
                Joiner.on("\n").join(getHeaders()),
                (readContent() == null) ? "" : new String(readContent()));
    }

}
