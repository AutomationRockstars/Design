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

import java.util.Map;

import groovy.lang.Closure;
import groovy.lang.Script;
public abstract class TestScriptBase extends Script{

	public String scriptName;
	public Object step(Object name, Object execute, Object verify){
		try {

		Object execCall = null;
		Object verifyCall = null;
        if (execute != null) {
        	execCall = ((Closure) execute).call();
	    }
        
        if (verify != null) {
        	verifyCall = ((Closure)verify).call(execCall);
        }
        return verifyCall;
		} finally {
			
		}
    }

    Object step(Map m){
        return step(m.get("title"),m.get("execute"),m.get("verify"));
    }
    
    Object step(String title, Map m){
    	return step(title,m.get("execute"),m.get("verify"));
    }
    
    public void testScript(String name){
    	scriptName = name;
    }
}
