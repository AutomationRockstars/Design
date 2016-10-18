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
package com.automationrockstars.gunter.events.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.JobScheduled;
import com.google.common.collect.Maps;

public class EventImplUtilsTest {

	@Test
	public void classFetching() {
		System.out.println(EventImplUtils.getClassForType(EventType.ACTION));
		System.out.println(EventImplUtils.getClassForType(EventType.JOB_SCHEDULED));
	}
	
	@Test
	public void should_keppMapIn(){
		Map<String,Object> params = Maps.newHashMap();
		params.put("String","string");
		params.put("int",7);
		params.put("List", Collections.singletonList(1221));
		
		JobScheduled js = (JobScheduledImpl) EventFactory.createJobScheduled("simple",params);
		
		String json = EventFactory.toJson(js);
		System.out.println(json);
		JobScheduled sj = EventFactory.fromJson(json);
		assertThat(sj.getProjectName(),is("simple"));
		assertThat(sj.getCause(),nullValue());
		assertThat(sj.getParameter("String").toString(), is("string"));
		assertThat((Integer)sj.getParameters().get("int"), is(7));
		assertThat((List<Integer>)sj.getParameters().get("List"),equalTo(Collections.singletonList(1221)));
		
		
	}	

}
