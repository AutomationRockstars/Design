package com.automationrockstars.gir.data;

import java.io.Closeable;

public interface TestDataService extends Closeable{

	<T extends TestDataRecord> TestData<T> provide(String name, boolean shared, Class<T > type);


	
	 
}
