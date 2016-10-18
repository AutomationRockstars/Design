package com.automationrockstars.bmo.cache;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class Bucket {


	private static List<String> requestCache = Lists.newCopyOnWriteArrayList();

	public static boolean cacheable(HttpObject request){
		if (HttpRequest.class.isAssignableFrom(request.getClass())){
			HttpMethod method = ((HttpRequest)request).getMethod();
			if (HttpMethod.GET.equals(method)
					|| HttpMethod.HEAD.equals(method)
					|| HttpMethod.CONNECT.equals(method)){
				return true;
			} else return false;			
		} else return false;
	}

	public static synchronized HttpResponse get(HttpObject request){
		LOG.debug("ID {}",(cacheable(request))?requestId(request) : "N/A" );
		if (! cacheable(request) || ! requestCache.contains(requestId(request))) return null ;
		else return SerializationFactory.load(requestId(request));
	}

	private static Logger LOG = LoggerFactory.getLogger(Bucket.class);

	@VisibleForTesting
	static boolean isEmpty(HttpContent content){
		return content.content().capacity() == 0;
	}

	static boolean isEmpty(HttpObject content){
		if (HttpContent.class.isAssignableFrom(content.getClass())){
			return isEmpty((HttpContent)content);
		} else throw new IllegalArgumentException(content + " is not content object");
	}
	public static synchronized void store(HttpRequest req,HttpObject httpObject){
		if (req == null || ! cacheable(req)) return;
		String requestId = requestId(req);;
		boolean canCache = req != null && cacheable(req);
		boolean canStore = 	HttpContent.class.isAssignableFrom(httpObject.getClass()) &&
							HttpMessage.class.isAssignableFrom(httpObject.getClass()) &&
							HttpResponse.class.isAssignableFrom(httpObject.getClass());
		LOG.warn(String.format("Request to %s can be cached %s", (req == null)?"null":req.getUri(),canCache));
		LOG.warn(String.format("Response %s can be stored %s", httpObject.getClass(),canStore));
		LOG.warn("Request ID {}",requestId);
		map(req,httpObject);
		LOG.warn(String.format("Response content %s message %s respnse %s class %s", 
				HttpContent.class.isAssignableFrom(httpObject.getClass()) ,
				HttpMessage.class.isAssignableFrom(httpObject.getClass()) ,
				HttpResponse.class.isAssignableFrom(httpObject.getClass()),
				httpObject.getClass()
				));
		
		
		
		
		
		if (HttpContent.class.isAssignableFrom(httpObject.getClass())){			
			if (HttpMessage.class.isAssignableFrom(httpObject.getClass())){
				HttpMessage message = (HttpMessage) httpObject;
				if (HttpResponse.class.isAssignableFrom(httpObject.getClass())){
					String saved = SerializationFactory.builder()
							.forRequest(requestId)
							.withContent(((HttpContent) httpObject).duplicate().content())
							.withStatus(((HttpResponse)message).getStatus())
							.withVersion(message.getProtocolVersion())
							.withHeaders(message.headers())
							.serialize().toString();
					LOG.debug("Response saved to {}",saved);
					requestCache.add(requestId);
				} else {
					LOG.debug("NOT RESPONSE {}",httpObject.getClass());
				}
				
			} else {
				LOG.debug("NOT MESSAGE {}",httpObject.getClass());
			}
		} else {
			LOG.debug("Something unknown {}",httpObject.getClass());

		}
	}
	public static String requestId(HttpObject request){
		Preconditions.checkArgument(HttpRequest.class.isAssignableFrom(request.getClass()),"%s is not a request",request);
		HttpRequest requestData = (HttpRequest) request;
		return DigestUtils.sha1Hex(requestData.getUri().getBytes());
	}
	private static Map<String,List> mapping = Maps.newConcurrentMap(); 
	public static void map(HttpRequest request, HttpObject response){
		if (cacheable(request)){
			String id = requestId(request);
			LOG.info("Request id {} to {}",id, request.getUri(),request.getMethod(),request.getProtocolVersion());
			if (mapping.get(id) == null){
				mapping.put(id, Lists.newCopyOnWriteArrayList());
			}
			mapping.get(id).add(response);
			if (HttpContent.class.isAssignableFrom(response.getClass())){
				ByteBuf buf = ((HttpContent)response).content();
				byte[] bytes = new byte[buf.readableBytes()];
				int readerIndex = buf.readerIndex();
				buf.getBytes(readerIndex, bytes);
				LOG.info("CNT : {}", new String(bytes));
			} else {
				LOG.info("RESPONSE {}",((HttpResponse) response).getStatus());
			}
			
			LOG.info("MAP {}",mapping);
		}
	}
}
