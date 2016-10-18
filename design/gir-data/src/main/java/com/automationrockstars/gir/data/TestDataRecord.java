package com.automationrockstars.gir.data;

import java.util.Map;

public interface TestDataRecord  {

	<T> T get(String name);
	
	Map<String,?> toMap();
}
