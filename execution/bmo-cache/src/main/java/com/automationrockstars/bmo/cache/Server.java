package com.automationrockstars.bmo.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import org.littleshoot.proxy.ChainedProxy;
import org.littleshoot.proxy.ChainedProxyManager;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.TransportProtocol;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import com.automationrockstars.base.ConfigLoader;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;



public class Server {

	private static HttpProxyServer server;
	public static void start() throws InterruptedException, ExecutionException, IOException{


		server =  DefaultHttpProxyServer.bootstrap()
				.withManInTheMiddle(new SelfSignedMitmManager())
				.withAddress(new InetSocketAddress(
						ConfigLoader.config().getString("server.ip","0.0.0.0")
						, ConfigLoader.config().getInt("server.port",80)))
//				.withChainProxyManager(new ChainedProxyManager() {
//					@Override
//					public void lookupChainedProxies(HttpRequest httpRequest, Queue<ChainedProxy> chainedProxies) {
//						chainedProxies.addAll(ChainedProxySelector.forRequest(httpRequest));
//					}
//				})
				.withFiltersSource(new HttpFiltersSourceAdapter() {
					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
						return new HttpFiltersAdapter(originalRequest) {
							@Override
							public HttpResponse requestPre(HttpObject httpObject) {
									return Bucket.get(httpObject);
							}

							@Override
							public HttpResponse requestPost(HttpObject httpObject) {
								return null;
							}

							@Override
							public HttpObject responsePre(HttpObject httpObject) {
								Bucket.store(originalRequest,httpObject);
								return httpObject;
							}

							@Override
							public HttpObject responsePost(HttpObject httpObject) {
								return httpObject;
							}   
						};
					}
				})
				.withTransportProtocol(TransportProtocol.TCP)
				.start();
	}

	public static void stop() throws InterruptedException, ExecutionException{
		server.stop();

	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException{
		Server.start();
		if (Files.notExists(Paths.get("server.lock"))){
			Files.createFile(Paths.get("server.lock"));
		}
		System.out.println("Started proxy on http://"+ ConfigLoader.config().getString("server.ip","0.0.0.0")
						+ ":" + ConfigLoader.config().getInt("server.port",80));
		System.out.println("Delete file <server.lock> to stop server");
		while (Paths.get("server.lock").toFile().exists()){
			Thread.sleep(5000);
		}
		Server.stop();
	}
}
