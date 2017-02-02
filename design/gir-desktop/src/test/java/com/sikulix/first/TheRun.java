package com.sikulix.first;
import org.junit.Test;
import org.sikuli.basics.Debug;

import com.automationrockstars.gir.desktop.ImageUiObject;
import com.automationrockstars.gir.desktop.Screen;
public class TheRun {

	@Test
	public void mainTest() throws Exception {
	    
		
		Debug.setDebugLevel(3);
	    
	    ImageUiObject someb = Screen.Finder.find("c:/tmp/tier1.png");
	    someb.click();
	    ImageUiObject someText = Screen.Finder.find("c:/tmp/margo.png");
	    someText.waitUntilVisible();
	    
	  }
	
}
