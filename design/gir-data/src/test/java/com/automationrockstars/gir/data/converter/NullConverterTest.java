package com.automationrockstars.gir.data.converter;

import static com.automationrockstars.asserts.Asserts.assertThat;

import org.junit.Test;

public class NullConverterTest {

	@Test
	public void test() {
		Integer val = NullConverter.nullConvert(int.class);
		assertThat("int",val == 0);
		byte b = NullConverter.nullConvert(byte.class);
		String nullString = NullConverter.nullConvert(String.class);
		
	}

}
