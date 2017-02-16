package com.automationrockstars.gir.ui;

import static com.automationrockstars.gir.ui.UiParts.*;
public class GoogleSearch {

	

	public static SearchResults performSearch(String query){
		on(GoogleHome.class).query().clear();
		on(GoogleHome.class).query().sendKeys(query);		
		on(GoogleHome.class).search().click();
		return get(SearchResults.class);
	}
}
