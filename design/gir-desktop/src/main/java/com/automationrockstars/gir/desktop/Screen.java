package com.automationrockstars.gir.desktop;

import com.automationrockstars.gir.desktop.internal.SikuliDriver;
import com.automationrockstars.gir.ui.UiPart;

public interface Screen extends UiPart {


	public static class Finder {
		public static ImageUiObject find(String path){
			return SikuliDriver.driver().findElement(path);
		}
	}
}
