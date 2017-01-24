package com.automationrockstars.gir.ui;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.automationrockstars.design.gir.webdriver.DriverFactory;
import com.google.common.base.Optional;
public class HeadTest {
	
	@BeforeClass
	public static void browser(){
		DriverFactory.getDriver().get("http://www.google.ie");
		
	}
	
	@AfterClass
	public static void stopBrowser(){
		DriverFactory.closeDriver();
	}

	@Test
	public void should_returnMeta(){
		assertThat(UiParts.head().meta().first().get().getAttribute("content"),not(isEmptyOrNullString()));
	}
	
	@Test
	public void should_returnTitle(){		
		assertThat(DriverFactory.getDriver().getTitle(),not(isEmptyOrNullString()));
	}
	
	@Test
	public void should_returnScript(){		
		assertThat(UiParts.head().script().first(),is(not(Optional.<WebElement>absent())));
	}
	
	@Test
	public void should_returnStyle(){	
		assertThat(UiParts.head().style().first(),is(not(Optional.<WebElement>absent())));
	}
}

