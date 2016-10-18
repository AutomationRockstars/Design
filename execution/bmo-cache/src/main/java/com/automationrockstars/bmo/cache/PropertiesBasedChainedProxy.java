package com.automationrockstars.bmo.cache;

import static com.automationrockstars.base.ConfigLoader.config;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLEngine;

import org.apache.commons.configuration.Configuration;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.littleshoot.proxy.TransportProtocol;
import org.littleshoot.proxy.ntlm.JcifsNtlmProvider;
import org.littleshoot.proxy.ntlm.NtlmHandler;
import org.littleshoot.proxy.ntlm.NtlmHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.annotations.VisibleForTesting;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

public class PropertiesBasedChainedProxy extends ChainedProxyAdapter{

	
	private final Logger LOG;
	private final String name;
	PropertiesBasedChainedProxy(String forName) {
		this.name= forName;
		 LOG = LoggerFactory.getLogger("Upstream proxy " + name);
		 populateFromProperties();
	}

	public String name(){
		return name;
	}
	
	public boolean applies(HttpRequest request){
		LOG.debug("Checking {} ",request);
		return request.getUri().matches(filter);
	}
	
	private String url;
	public void url(String url){
		this.url = url;
	}
	private String filter; 
	public void filter(String filter){
		this.filter = filter;
	}
	
	private boolean ntlm = false;
	public void ntlm(){
		ntlm=true;
	}

	public void basic(){
		ntlm=false;
	}
	private String user; 
	public void user(String user){
		this.user=user;
	}
	private String pass;
	public void pass(String pass){
		this.pass = pass;
	}
	private String domain;
	public void domain(String domain){
		this.domain = domain;
	}
	
	
	
	@Override
	public InetSocketAddress getChainedProxyAddress() {
		return new InetSocketAddress(this.url.split(":")[0],Integer.valueOf(this.url.split(":")[1]));
	}



	@Override
	public TransportProtocol getTransportProtocol() {
		return TransportProtocol.TCP;
	}

	@Override
	public boolean requiresEncryption() {
		return false;
	}


	@Override
	public SSLEngine newSslEngine() {
		return null;
	}

	@Override
	public void filterRequest(HttpObject httpObject) {
		 if (httpObject instanceof DefaultHttpRequest ){
			 LOG.debug("Processing request to {}",((DefaultHttpRequest) httpObject).getUri());	 
		 }
	}

	@Override
	public void connectionSucceeded() {
		LOG.debug("Connected to proxy {}",name);
	}

	@Override
	public void connectionFailed(Throwable cause) {
		LOG.error("Proxy with name {} failed",name,cause);
	}


	private NtlmHandler ntlmHandler = null;

	
	@Override
	public NtlmHandler getNtlmHandler() {
		if (ntlm && ntlmHandler == null){
			ntlmHandler = new NtlmHandlerImpl(new JcifsNtlmProvider(user, pass,domain));
		}
		return ntlmHandler;
	}

	@VisibleForTesting
	final void populateFromProperties(){
		Configuration proxyConfig = config().subset("proxy."+name);
		LOG.debug("Proxy {} properties:\n {}",name,ConfigLoader.logConfig(proxyConfig));
		if ("ntlm".equalsIgnoreCase(proxyConfig.getString("type"))){
			ntlm();
		} else {
			basic();
		}
		user(proxyConfig.getString("user"));
		pass(proxyConfig.getString("password"));
		url(proxyConfig.getString("url"));
		domain(proxyConfig.getString("domain"));
		filter(proxyConfig.getString("filter"));
		
	}
	
	public String toString(){
		return String.format("name: %s, url: %s,filter: %s", name,url,filter);
	}
	
	
}
