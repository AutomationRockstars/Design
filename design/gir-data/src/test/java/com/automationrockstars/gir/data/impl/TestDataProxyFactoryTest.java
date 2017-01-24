package com.automationrockstars.gir.data.impl;

import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.automationrockstars.gir.data.TestDataRecord;

public class TestDataProxyFactoryTest {

	static interface IsTestDataRecord extends TestDataRecord {
		
		String id();
	}
	
	static interface NotTestDataRecord {
		String id();
	}
	@Test
	public void should_verifyTestDataRecordClasses() {
		assertThat(TestDataProxyFactory.isTestDataRecord(IsTestDataRecord.class), is(true));
		assertThat(TestDataProxyFactory.isTestDataRecord(NotTestDataRecord.class), is(false));
		assertThat(TestDataProxyFactory.isTestDataRecord(TestDataRecord.class), is(true));
	}

	@Test
	public void should_getPropertyName() {
		assertThat(TestDataProxyFactory.getPropertyName("lowerUpper"),is(equalTo("lower upper")));
		assertThat(TestDataProxyFactory.getPropertyName("UowerUpper"),is(equalTo("uower upper")));
		assertThat(TestDataProxyFactory.getPropertyName("getLowerUpper"),is(equalTo("lower upper")));
	}
	

}
