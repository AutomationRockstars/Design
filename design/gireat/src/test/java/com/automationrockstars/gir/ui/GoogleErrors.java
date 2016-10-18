package com.automationrockstars.gir.ui;

import java.util.List;

import com.google.common.collect.Lists;

public class GoogleErrors {

	@Globals
	public static List< org.openqa.selenium.By> errorElements(){
		return Lists.newArrayList(
				new FilteredBy(org.openqa.selenium.By.tagName("div"), "text.contains('Error')"),
				org.openqa.selenium.By.partialLinkText("Error")
				);
	}
}
