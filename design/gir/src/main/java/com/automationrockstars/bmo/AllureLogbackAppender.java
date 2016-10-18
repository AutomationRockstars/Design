/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.bmo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.MakeAttachmentEvent;

public class AllureLogbackAppender<E> extends AppenderBase<E>  {

	private static ByteArrayOutputStream attachment = new ByteArrayOutputStream();
	private static boolean empty = true;
	private static AllureLogbackAppender<?> instance;
	private Encoder<E> encoder;
	public AllureLogbackAppender() {
		this.name=this.getClass().getName();
		instance = this;
	}

	@Override
	protected void append(E eventObject) {
		try {

			encoder.doEncode(eventObject);
			empty = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setEncoder(Encoder<E> enc){
		encoder=enc;
		try {
			encoder.init(attachment);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean isEmpty(){
		return empty;
	}
	public static void fire(String name) {
		byte[] attachmentBody = attachment.toByteArray();
		Allure.LIFECYCLE.fire(new MakeAttachmentEvent(attachmentBody, name+"Log", "text/plain"));
		attachment.reset();
		empty=true;
	}
	

}
