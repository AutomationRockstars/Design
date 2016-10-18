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
package com.automationrockstars.design.gir.webdriver.plugin;

import com.automationrockstars.design.gir.webdriver.UiObject;

public interface UiObjectActionPlugin {

	void beforeClick(UiObject element);
	void afterClick(UiObject element);
	
	void beforeSubmit(UiObject element);
	void afterSubmit(UiObject element);
	
	void beforeSendKeys(UiObject element,CharSequence... keysToSend );
	void afterSendKeys(UiObject element,CharSequence... keysToSend );
	
	void beforeClear(UiObject element);
	void afterClear(UiObject element);
	
}
