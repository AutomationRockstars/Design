package com.sikulix.first;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.automationrockstars.design.desktop.driver.ImageUiObject;
import com.automationrockstars.design.desktop.driver.internal.SikuliDriver;
import com.automationrockstars.gir.desktop.ExtendedUiParts;
public class TheRun {

	@Test
	public void mainTest() throws Exception {
	    
		
		
	    /**
		ImageUiObject someText = SikuliDriver.driver().findElement("c:/tmp/margo.png");
		((SikuliImageUiObject)someText).getWrappedElement().highlight(5);
		System.out.println("dfcsfssdfs" + someText.isVisible());
		
		
		
		WebElement sss = SikuliDriver.driver().findElement(new ByImage("c:/tmp/tier1.png"));
		System.out.println(sss.getText());
		sss.click();
		
		
		
		
		
		
		someText = SikuliDriver.driver().findElement("c:/tmp/margo.png");
		((SikuliImageUiObject)someText).getWrappedElement().highlight(5);
		ImageUiObject oo;
		Iterator<ImageUiObject> oos = SikuliDriver.driver().findElements("c:/tmp/margo.png");
		while ( oos.hasNext() ){
			
			((SikuliImageUiObject)oos.next()).highlight();
		}
		**/
		
//	    ImageUiObject someb = Screen.Finder.find("c:/tmp/tier1.png");
//	    someb.click();
//	    ImageUiObject someText = SikuliDriver.driver().findElement("c:/tmp/margo.png");
//	    someText.waitUntilVisible();
		
		
		
		ExtendedUiParts.on(Nic.class).dd().click();
	    }
	
}
