package com.automationrockstars.gir.desktop.internal;

import org.openqa.selenium.interactions.Keyboard;
import org.sikuli.script.Region;

import com.google.common.base.Joiner;

public class SikuliKeyboard implements Keyboard{

	private final Region region;
	
	public SikuliKeyboard() {
		this(SikuliDriver.screen());
	}
	public SikuliKeyboard(Region region) {
		this.region = region;
	}

	
	@Override
	public void sendKeys(CharSequence... keysToSend) {
		region.type(Joiner.on("").join(keysToSend));
		
	}

	@Override
	public void pressKey(CharSequence keyToPress) {
		region.keyDown(keyToPress.toString());
		
	}

	@Override
	public void releaseKey(CharSequence keyToRelease) {
		region.keyUp(keyToRelease.toString());
		
	}

}
