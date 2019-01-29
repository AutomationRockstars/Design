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
package com.automationrockstars.gunter.events.impl;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Attachment;

public class AttachmentImpl extends AbstractTestEvent implements Attachment {

    private byte[] content;
    private String mimeType;
    private String title;

    public AttachmentImpl() {
        super();
    }

    public AttachmentImpl(String parentId) {
        super(parentId);
    }

    public void attach(String mimeType, String title, byte[] content) {
        setMimeType(mimeType);
        setTitle(title);
        setContent(content);
    }

    @Override
    public EventType getType() {
        return EventType.ATTACHMENT;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public void setMimeType(String type) {
        this.mimeType = type;

    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }


}
