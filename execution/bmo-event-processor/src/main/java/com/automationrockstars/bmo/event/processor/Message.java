package com.automationrockstars.bmo.event.processor;

import org.webbitserver.HttpRequest;

import com.automationrockstars.gunter.events.Event;

public interface Message {
	Event toEvent();
	HttpContent toHttpContent();
	HttpRequest toHttpRequest();
	void withHttpResponseHandler(HttpResponseHandler handler);

	public static class Builder implements Message{
		private Event event = null;
		private HttpContent content = null;
		private HttpRequest request = null;
		private HttpResponseHandler handler = null;
		private Builder(){	
		}
		
		public static Builder newMessage(){
			return new Builder();
		}
		
		public Builder withEvent(Event event){
			this.event = event;
			return this;
		}
		
		public Builder withHttpContent(HttpContent toSend){
			this.content=toSend;
			return this;
		}

		public Builder withHttpRequest(HttpRequest request2){
			this.request = request2;
			return this;
		}

		@Override
		public Event toEvent() {
			return event;
		}

		@Override
		public void withHttpResponseHandler(HttpResponseHandler handler) {
			this.handler = handler;
		}

		public HttpResponseHandler handler(){
			return handler;
		}
		
		
		@Override
		public HttpContent toHttpContent() {
			return content;
		}

		
		@Override
		public HttpRequest toHttpRequest() {
			return request;
		}
		
		public String toString(){
			return String.format("Event: %s, Http: %s", event,(request==null)?content:request);
		}
	}

	
	
	
	public static Message NULL = new Message(){

		@Override
		public Event toEvent() {
			return null;
		}


		@Override
		public void withHttpResponseHandler(HttpResponseHandler handler) {
		}

		@Override
		public HttpContent toHttpContent() {
			return null;
		}

		

		@Override
		public HttpRequest toHttpRequest() {
			return null;
		}
		
		public String toString(){
			return "Null message";
		}
		
	};
}
