package com.automationrockstars.gir.desktop.internal;

import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;

public class SikuliScreenMouse implements Mouse {

	private final static SikuliMouse mouse = new SikuliMouse(SikuliDriver.screen());

	public void click(Coordinates where) {
		mouse.click(where);
	}

	public void doubleClick(Coordinates where) {
		mouse.doubleClick(where);
	}

	public void mouseDown(Coordinates where) {
		mouse.mouseDown(where);
	}

	public void mouseUp(Coordinates where) {
		mouse.mouseUp(where);
	}

	public void mouseMove(Coordinates where) {
		mouse.mouseMove(where);
	}

	public void mouseMove(Coordinates where, long xOffset, long yOffset) {
		mouse.mouseMove(where, xOffset, yOffset);
	}

	public void contextClick(Coordinates where) {
		mouse.contextClick(where);
	}


}
