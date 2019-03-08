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
package com.automationrockstars.gunter.events;

public interface Attachment extends Event {

    byte[] getContent();

    void setContent(byte[] content);

    String getMimeType();

    void setMimeType(String type);

    String getTitle();

    void setTitle(String title);

    void attach(String mimeType, String title, byte[] content);
}
