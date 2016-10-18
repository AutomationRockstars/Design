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
package com.automationrockstars.gir.mobile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import static com.automationrockstars.gir.mobile.MobileUiObjectFactory.*;
public class Debug {

	public static void runFromFile(Path file){
		try {
			for (String object : Files.readAllLines(file, Charset.defaultCharset())){
				if (object.equals("SOURCE")){
					System.out.println(PageUtils.getPageSource());
				} else {
					try {
						if (object.contains("KEYS")){
							String obj = object.split("KEYS")[0];
							onAny(obj).waitForVisible();
							onAny(obj).sendKeys(object.split("KEYS")[1]);
						} else {
							onAny(object).waitForVisible();
							onAny(object).click();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException f) {
			f.printStackTrace();
		}
	}

}
