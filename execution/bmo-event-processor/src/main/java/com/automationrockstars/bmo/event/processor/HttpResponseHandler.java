package com.automationrockstars.bmo.event.processor;

import org.apache.http.client.fluent.Response;

public interface HttpResponseHandler {

	void on(Response response);
}
