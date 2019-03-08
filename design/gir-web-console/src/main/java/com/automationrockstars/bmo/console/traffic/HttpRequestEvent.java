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
import org.apache.http.RequestLine;

public class HttpRequestEvent extends AbstractHttpEvent {

    private RequestLine requestLine;

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public void setRequestLine(RequestLine line) {
        this.requestLine = line;
    }

    public String toString() {
        return String.format("%s\n%s\n%s",
                getRequestLine(),
                Joiner.on("\n").join(getHeaders()),
                (readContent() == null) ? "" : new String(readContent()));
    }
}
