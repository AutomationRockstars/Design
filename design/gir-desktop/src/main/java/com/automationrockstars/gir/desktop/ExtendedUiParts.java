package com.automationrockstars.gir.desktop;

import java.lang.reflect.Proxy;

import com.automationrockstars.gir.desktop.internal.ScreenProxy;
import com.automationrockstars.gir.ui.UiPart;
import com.automationrockstars.gir.ui.UiParts;

public class ExtendedUiParts extends UiParts {

	@SuppressWarnings("unchecked")
	public static <T extends UiPart> T get(Class<T> part){
		if (ExtendedUiPart.class.isAssignableFrom(part)){
			return (T) Proxy.newProxyInstance(part.getClassLoader(), new Class[] {part}, new ScreenProxy((Class<? extends ExtendedUiPart>) part));
		}
		else return UiParts.get(part);
	}

	public static <T extends UiPart> T on(Class<T> uiPart){
		return ExtendedUiParts.get(uiPart);
	}

	
}
