package com.automationrockstars.bmo.event.processor.internal;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import com.automationrockstars.bmo.event.processor.Message;

public class CatchAllHttpListener implements HttpHandler{

	private static CatchAllHttpListener INSTANCE;
	public static final CatchAllHttpListener get(){
		if (INSTANCE == null){
			INSTANCE = new CatchAllHttpListener();
		}
		return INSTANCE;
	}
	@Override
	public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
		ProcessorFactory.process(Message.Builder.newMessage().withHttpRequest(request), request.method(),request.uri());
		response.end();
	}
	


}
