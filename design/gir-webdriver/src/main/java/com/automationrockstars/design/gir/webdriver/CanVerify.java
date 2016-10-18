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
package com.automationrockstars.design.gir.webdriver;

import org.openqa.selenium.By;

public interface CanVerify {

	void verifyVisible();
	
	void verifyVisible(String failMessage);
	
	void verifyVisible(String failMessage, HasLocator errorObject);
	
	void verifyVisible(String failMessage, By errorObject);
	
	
	
}
