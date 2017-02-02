package com.automationrockstars.gir.desktop.internal;

import org.openqa.selenium.interactions.Keyboard;

public class SikuliScreenKeyboard implements Keyboard {

	private static final SikuliKeyboard keyboard = new SikuliKeyboard();

	public void sendKeys(CharSequence... keysToSend) {
		keyboard.sendKeys(keysToSend);
	}

	public void pressKey(CharSequence keyToPress) {
		keyboard.pressKey(keyToPress);
	}

	public void releaseKey(CharSequence keyToRelease) {
		keyboard.releaseKey(keyToRelease);
	}
	
	
}
