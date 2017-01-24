package com.automationrockstars.gir.data;

import java.io.Closeable;
import java.io.InputStream;

public interface TestDataService extends Closeable{

	String name();
	
	<T extends TestDataRecord> TestData<T> provide(String name, boolean shared, Class<T > type);

	void loadFrom(String file);
	
	void loadFrom(InputStream content);
	
	
	 
}
