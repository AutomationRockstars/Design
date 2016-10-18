package com.automationrockstars.gir.data.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.TestDataService;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;

public class SimpleTestDataService implements TestDataService{

	@Override
	public void close() throws IOException {
		Files.write(new GsonBuilder().setPrettyPrinting().create().toJson(DataLoader.cache()),Paths.get("output.json").toFile(),Charset.defaultCharset());		
	}

	@Override
	public <T extends TestDataRecord> TestData<T> provide(String name, boolean shared, Class<T> type) {
		if ("DEFAULT".equals(name)){
			if (! DataLoader.cache().containsKey(type)){
				DataLoader.cache().put(type, new SimpleTestData<T>(shared,type));
			}
			return (TestData<T>) DataLoader.cache().get(type);
		}
		else return null;
	}

	

}
