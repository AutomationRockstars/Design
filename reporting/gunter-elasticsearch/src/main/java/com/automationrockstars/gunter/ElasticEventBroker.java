package com.automationrockstars.gunter;

import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.EventBroker;
import com.automationrockstars.gunter.events.EventBus;
import com.automationrockstars.gunter.events.EventFactory;

import java.io.IOException;


public class ElasticEventBroker implements EventBroker {

    public ElasticEventBroker(){
        EventBus.registerBroker(this);
    }

    @Override
    public void fireEvent(String event) {
        ElasticSender.send(EventFactory.fromJson(event));
    }

    @Override
    public void fireEvent(Event event) {
        ElasticSender.send(event);
    }

    @Override
    public void close() throws IOException {
        ElasticSender.close();
    }
}
