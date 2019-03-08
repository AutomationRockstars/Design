package com.automationrockstars.bmo.cache;

import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.*;

public class ChainedProxySelectorTest {

    @Test
    public void should_constructProxiesFromProperties() {
        assertThat(ChainedProxySelector.proxyNames(), containsInAnyOrder("aws", "bluecoat", "local"));
    }

}
