package com.automationrockstars.bmo.cache;

import java.util.List;
import java.util.Set;

import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import io.netty.handler.codec.http.HttpRequest;

public class ChainedProxySelector {

	private static Logger LOG = LoggerFactory.getLogger(ChainedProxySelector.class);

	private static String local(){
		return ConfigLoader.config().getString("proxy.local");
	}
	public static synchronized List<ChainedProxy> forRequest(HttpRequest request){
		List<ChainedProxy> result = Lists.newArrayList();
		String uri = request.getUri();
		LOG.info("Processing request to {}",uri);
		if (ConfigLoader.config().containsKey("proxy.local") && uri.matches(local())){
			result.add(ChainedProxyAdapter.FALLBACK_TO_DIRECT_CONNECTION);
			LOG.info("Using direct connection for {}",uri);
		} else {
			if (proxyNames().isEmpty()){
				LOG.info("No upstream proxy defined. Using direct connection");
				result.add(ChainedProxyAdapter.FALLBACK_TO_DIRECT_CONNECTION);	
			}
			for (String proxyName : proxyNames()){
				if (ConfigLoader.config().containsKey(String.format("proxy.%s.filter",proxyName))
						&& uri.matches(ConfigLoader.config().getString(String.format("proxy.%s.filter",proxyName)))){
					result.add(new PropertiesBasedChainedProxy(proxyName));
					LOG.info("Using proxy {} for {}",result,uri);
				}				
			}
		}
		LOG.debug("Created upstream proxies {}",result);
		return result;
	}


	@VisibleForTesting
	static List<String> proxyNames(){
		List<String> proxyKeys = Lists.newArrayList(ConfigLoader.config().subset("proxy").getKeys());
		Set<String> proxyNames = Sets.newHashSet();
		proxyNames.remove("local");
		for (String proxyKey : proxyKeys){
			proxyNames.add(proxyKey.split("\\.")[0]);
		}
		return Lists.newArrayList(proxyNames);
	}

}
