package com.automationrockstars.gunter;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.gunter.events.EventFactory;
import org.junit.Test;

/**
 * Created by 47098 on 19/09/2017.
 */
public class ElasticSenderTest {

    @Test
    public void should_sendEvent(){
        ConfigLoader.config().addProperty(ElasticSender.ES_URI_KEY,"http://localhost:9200/ci/jobs/");
        ElasticSender.send(EventFactory.createExecutionStart("TestExecution"));
    }
}
