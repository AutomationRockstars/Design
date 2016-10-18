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
package com.automationrockstars.gunter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IdUtils {

	public static final String id(EventType type){
		return String.format("%s-%s", type,postfix());
	}
	
	public static final String id(String parent, EventType type){
		return String.format("%s-%s-%s", parent, type,postfix());
		
	}
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd.HH:mm:ss");
	private static synchronized final String postfix(){
		return formatter.format(new Date())  + String.valueOf(System.nanoTime()).substring(12);
	}
	
}
