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
package com.automationrockstars.gunter.jenkins;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

import com.automationrockstars.gunter.jenkins.rabbitmq.GunterJobListener;
import com.google.common.collect.Lists;

import hudson.Extension;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Project;
import hudson.model.StringParameterValue;
import hudson.triggers.TriggerDescriptor;
import jenkins.model.Jenkins;

@Extension
public class GunterJobTrigger extends GunterJobListener{


	@DataBoundConstructor
	public GunterJobTrigger() {
		super();
	}

	private static final Cause cause(final String causeString){
		return new Cause() {

			@Override
			public String getShortDescription() {
				return causeString;
			}
		};
	}
	private static final List<ParameterValue> params(Map<String,Object> params){
		List<ParameterValue> result = Lists.newArrayList();
		for (Map.Entry<String, Object> entry : params.entrySet()){
			result.add(new StringParameterValue(entry.getKey(),Objects.toString(entry.getValue()))); 
		}
		return result;
	}
	public void runJob(String project, Map<String,Object> parametes){
		LOG.info("Schedduling build on " + project);
		Item item = Jenkins.getInstance().getItem(project);
		if (item != null && item instanceof Project){
			Project projectInstance = (Project) item;
			try {
				if (projectInstance.getTrigger(GunterJobTrigger.class) == null){
					projectInstance.addTrigger(this);
					LOG.info("Trigger added");
				}
			} catch (IOException e) {			
				LOG.log(Level.SEVERE,"Cannot initialize Event trigger on project " + project,e);
			}
			if (parametes == null){
				projectInstance.scheduleBuild(0,cause("Eventr trigger"));
				LOG.info("job scheduled without parameters");
			} else {
				boolean started = false;
				try{ 
				started = projectInstance.scheduleBuild(0, cause("Event trigger"), new ParametersAction(params(parametes)));
				} catch (Throwable whatever){
					LOG.log(Level.SEVERE,"Problem scheduling build ", whatever);
				}
				LOG.info("Job scheduled with parameters: " + started);
			}
		}
	}


	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * The descriptor for this trigger.
	 *
	 * @author rinrinne a.k.a. rin_ne
	 */
	@Extension
	public static class DescriptorImpl extends TriggerDescriptor {

		@Override
		public boolean isApplicable(Item item) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Gunter build trigger";
		}


	}

	private static final Logger LOG = Logger.getLogger(GunterJobTrigger.class.getName());


}
