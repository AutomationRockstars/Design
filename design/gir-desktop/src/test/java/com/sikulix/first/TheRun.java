package com.sikulix.first;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.automationrockstars.gir.desktop.ImageUiObject;
import com.automationrockstars.gir.desktop.internal.SikuliDriver;
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
		
		
		
//		ExtendedUiParts.on(Nic.class).dd().click();
	    ImageUiObject nic = SikuliDriver.driver().findElement("c:/tmp/fulljson.png");
	    System.out.println(nic.getText());
		new Actions(nic.getKeyboard(),nic.getMouse()).moveToElement(nic).click().perform();
	  }
	
}
