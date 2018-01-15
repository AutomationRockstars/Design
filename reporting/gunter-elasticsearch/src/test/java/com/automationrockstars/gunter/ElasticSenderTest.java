package com.automationrockstars.gunter;

import com.automationrockstars.asserts.Asserts;
import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.events.EventFactory;
import org.junit.Test;

public class ElasticSenderTest {

    @Test
    public void should_sendEvent(){
        ConfigLoader.config().addProperty(ElasticSender.ES_URI_PARAM,"localhost:9200");
        ElasticSender.send(EventFactory.createExecutionStart("TestExecution"));

    }


    @Test
    public void should_detectData(){
        ConfigLoader.config().addProperty(ElasticSender.ES_URI_PARAM,"localhost:9300");
        Asserts.assertThat("Data connection failed",ElasticSender.canConnectViaData());
        Asserts.assertThat("REST connection didnt fail",!ElasticSender.isRest());
    }

    @Test
    public void should_detectRest(){
        ConfigLoader.config().addProperty(ElasticSender.ES_URI_PARAM,"localhost:9200");
        Asserts.assertThat("REST connection failed",ElasticSender.canConnectViaRest());
        Asserts.assertThat("DATA connection didn't fail",! ElasticSender.canConnectViaData());
        Asserts.assertThat("REST connection failed",ElasticSender.isRest());
    }


    @Test
    public void should_doClientSend(){
        ConfigLoader.config().addProperty(ElasticSender.ES_URI_PARAM,"localhost:9300");
        ElasticSender.dataSend(EventFactory.createExecutionStart("TestExec"));
    }
}


