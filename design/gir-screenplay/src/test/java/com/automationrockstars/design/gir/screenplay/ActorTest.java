package com.automationrockstars.design.gir.screenplay;

import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;


public class ActorTest {

    @Test
    public void should_changeActorsAndData() {
        Actor.clean();
        System.setProperty(Actor.ACTOR_PARTS_PROP,"data,unit,actor");
        Actor initialAla = Actor.forName("Ala");
        assertThat(Actor.forName("Ala").currentRecordFor(SimpleActor.class).name(),is("Unit Ala"));
        Actor.clean();
        System.setProperty(Actor.ACTOR_PARTS_PROP,"data,other,actor");
        assertThat(initialAla, is(not(Actor.forName("Ala"))));
        assertThat(Actor.forName("Ala").currentRecordFor(SimpleActor.class).name(),is("Other Ala"));
    }



    @Test
    public void should_changeActorList() {
        Actor.clean();
        System.setProperty(Actor.ACTOR_PARTS_PROP,"data,unit,actor");
        assertThat(Actor.supportedActors(), contains("Ola","Ala"));
        assertThat(Actor.supportedActors(), not(contains("Alan")));
        Actor.clean();
        System.setProperty(Actor.ACTOR_PARTS_PROP,"data,other,actor");
        assertThat(Actor.supportedActors(), contains("Ala","Alan"));
        assertThat(Actor.supportedActors(), not(contains("Ola")));
    }
}