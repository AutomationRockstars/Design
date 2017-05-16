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

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
public class IdUtilsTest {

	@Test
	public void testIdEventType() {
		assertThat(IdUtils.id(EventType.ACTION),is(not(nullValue())));
	}

	@Test
	public void testIdStringEventType() {
		assertThat(IdUtils.id(IdUtils.id(EventType.ACTION),EventType.ACTION),is(not(nullValue())));
	}

}
