package com.automationrockstars.gir.ui;

import org.openqa.selenium.By.ByName;

import com.automationrockstars.design.gir.webdriver.InitialPage;

import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.TextInput;


@InitialPage
@Name("Google initial page")
public interface GoogleHome extends UiPart{
	
	@Find(@By(how=ByName.class,using="q"))
	TextInput query();
	
	
	@Find(value={
			@By(how=ByName.class,using="btnK"),
			@By(how=ByName.class,using="btnG")},
			any=true)
	Button search();
	
	
	

}
