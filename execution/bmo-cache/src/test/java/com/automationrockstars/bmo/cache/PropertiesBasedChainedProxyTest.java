package com.automationrockstars.bmo.cache;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.net.InetSocketAddress;

import org.junit.Test;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
public class PropertiesBasedChainedProxyTest {

	private static PropertiesBasedChainedProxy proxy = new PropertiesBasedChainedProxy("bluecoat"); 
	@Test
	public void should_readAllNtlmProps() {
		assertThat(proxy.getNtlmHandler(), is(notNullValue()));
	}
	
	@Test
	public void should_readUrl(){
		assertThat(proxy.getChainedProxyAddress(), is(new InetSocketAddress("5.5.5.5",80)));
	}
	
	@Test
	public void should_filterPositivel(){
		assertThat(proxy.applies(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "anything/do")),is(false));
	}
	
	@Test
	public  void should_filterNegatively(){
		assertThat(proxy.applies(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "bluecoat/do")),is(true));
	}

}
