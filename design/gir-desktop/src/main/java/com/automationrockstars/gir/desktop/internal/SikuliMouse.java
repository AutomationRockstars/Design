package com.automationrockstars.gir.desktop.internal;

import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.sikuli.script.Button;
import org.sikuli.script.Region;

public class SikuliMouse implements Mouse {

	
	private Region region;
	
	
	public SikuliMouse(Region region) {
		this.region = region;
	}

	@Override
	public void click(Coordinates where) {
		region.click();
		
	}

	@Override
	public void doubleClick(Coordinates where) {
		region.doubleClick();
		
	}

	@Override
	public void mouseDown(Coordinates where) {
		region.mouseDown(Button.LEFT);
		
	}

	@Override
	public void mouseUp(Coordinates where) {
		region.mouseUp();
		
	}

	@Override
	public void mouseMove(Coordinates where) {
		region.mouseDown(Button.LEFT);
		
	}

	@Override
	public void mouseMove(Coordinates where, long xOffset, long yOffset) {
		region.mouseMove((int) xOffset,(int) yOffset);
		
	}

	@Override
	public void contextClick(Coordinates where) {
		region.mouseDown(Button.RIGHT);
		region.mouseUp();
		
	}

}
