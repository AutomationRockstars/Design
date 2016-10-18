package com.automationrockstars.bmo.cache;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class SerializationFactory {
	private static final Gson mapper = new Gson();
	private static final String RESPONSE_FILE = "response";
	private static final String CONTENT_FILE = "content";
	private static final String CACHE = "bucket";
	private static final Logger LOG = LoggerFactory.getLogger(SerializationFactory.class);

	public static SerializableResponseBuilder builder(){
		return new SerializableResponseBuilder();
	}
	
	public static class SerializableResponseBuilder {
		
		private SerializableResponse response = new SerializableResponse();
		
		public SerializableResponse build(){
			return response;
		}
		public SerializableResponseBuilder forRequest(String request){
			response.setRequest(request);
			return this;
		}
		
		public SerializableResponseBuilder withContentFile(String filePath){
			response.setContentFile(filePath);
			return this;
		}
		
		public SerializableResponseBuilder withHeaders(HttpHeaders headers){
			response.setHeaders(headers);
			return this;
		}
		
		public SerializableResponseBuilder withVersion(HttpVersion version){
			response.setVersion(version);
			return this;
		}
		
		public SerializableResponseBuilder withStatus(HttpResponseStatus status){
			response.setStatus(status);
			return this;
		}
		
		public SerializableResponseBuilder withContent(ByteBuf content){
			response.setContentFile(contentToFile(content).toString());
			return this;
		}
		
		public Path serialize(){
			return responseToFile(response);
		}
	}
	
	public static Path contentToFile(ByteBuf content){
		Path target = Paths.get(CACHE,CONTENT_FILE,UUID.randomUUID().toString());
		try (FileChannel contentFile = FileChannel.open(target, StandardOpenOption.CREATE)){
			contentFile.write(content.nioBuffer());
		} catch (IOException e) {
			LOG.error("Cannot save content");
			Throwables.propagate(e);
		}
		return target;
	}
	
	public static Path responseToFile(SerializableResponse response){
		Path target = null;
		try {
			target = Paths.get(CACHE,response.getRequest(),RESPONSE_FILE);
			LOG.debug("Writing to {}",target);
			Files.write(target,mapper.toJson(response).getBytes(),StandardOpenOption.CREATE);
		} catch (IOException e) {
			LOG.error("Response {} cannot be writtern due to {}",response,e.getMessage());
		}
		return target;
	}
	
	public static FullHttpResponse load(String id){
		DefaultFullHttpResponse result = null;
		try {
			SerializableResponse responseData = mapper.fromJson(Files.newBufferedReader(Paths.get(CACHE,id,RESPONSE_FILE), Charset.defaultCharset()), SerializableResponse.class);
			Path contentPath = Paths.get(responseData.getContentFile()); 			
			ByteBuf content = Unpooled.wrappedBuffer(FileChannel.open(contentPath, StandardOpenOption.READ).map(MapMode.READ_ONLY, 0, FileUtils.sizeOf(contentPath.toFile())));
			result = new DefaultFullHttpResponse(responseData.getVersion(), responseData.getStatus(),content);
			result.trailingHeaders().set(responseData.getHeaders());
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			LOG.error("Cannot deserialize the response",e);
		}
		
		return result;
	}

}
