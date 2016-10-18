package com.automationrockstars.bmo.cache;

import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ChainedProxySelectorTest {

	@Test
	public void should_constructProxiesFromProperties() {
		assertThat(ChainedProxySelector.proxyNames(),containsInAnyOrder("aws","bluecoat","local"));
	}

}
