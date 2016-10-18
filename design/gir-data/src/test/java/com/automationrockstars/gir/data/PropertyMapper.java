package com.automationrockstars.gir.data;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class PropertyMapper {

	@Test
	public void checkTheThingy(){
		System.out.println(StringUtils.getLevenshteinDistance("license type", "licenseType"));
	}
}
