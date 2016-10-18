package com.automationrockstars.bmo.event.processor;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Collections;

import org.junit.Test;

import com.automationrockstars.bmo.event.processor.Action;
import com.automationrockstars.bmo.event.processor.Message;
import com.automationrockstars.bmo.event.processor.SelectiveActionBuilder;
import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Commit;
import com.automationrockstars.gunter.events.Event;
import com.automationrockstars.gunter.events.EventFactory;
import com.automationrockstars.gunter.events.TestExecutionFinish;
import com.automationrockstars.gunter.events.TestExecutionStart;
import com.automationrockstars.gunter.events.TestSuiteFinish;
import com.automationrockstars.gunter.events.TestSuiteStart;

public class SelectiveActionBuilderTest {

	private static boolean commit = false;
	private static boolean executionStart = false;
	private static boolean executionFinished = false;
	
	private static Event commitEvent = EventFactory.createCommit("", Collections.singletonMap("a", "a"));
	private static Event execStartEvent = EventFactory.createExecutionStart("aa");
	private static Event execFinishEvent = EventFactory.createExecutionFinish((TestExecutionStart) execStartEvent, "DONE");
	private static Event suiteStart = EventFactory.createSuiteStart((TestExecutionStart) execStartEvent, "aaa");
	private static Event suiteFinish = EventFactory.createSuiteFinish((TestSuiteStart) suiteStart, "DONE");
	private static Event log = EventFactory.createLog(suiteStart, "INFO", "TEST", "aaaa");
	private static Event action = EventFactory.createAction(suiteStart, "action", "aaaaa");
	
	private static SelectiveActionBuilder b = SelectiveActionBuilder.newRule()
			.on(Commit.class).run(new Action<Commit>() {
				@Override
				public Message process(Commit event) {
					commit = true;
					return null;
				}})
			.on(TestExecutionStart.class).run(new Action<TestExecutionStart>() {

				@Override
				public Message process(TestExecutionStart event) {
					executionStart = true;
					return null;
				}})
			.on(TestExecutionFinish.class).run(new Action<Event>() {

				@Override
				public Message process(Event event) {
					executionFinished = true;
					return Message.NULL;
				}})
			.on(TestSuiteStart.class).run(Action.PASS_TROUGH)
			.on(TestSuiteFinish.class).run(Action.DO_NOTHING)
			.on(EventType.TEST_LOG).run(Action.PASS_TROUGH)
			.on(SelectiveActionBuilder.predicate(true)).run(Action.PASS_TROUGH);
			
			
	
	@Test
	public void should_reactOnDifferentEvents() {
		Message result = b.process(execFinishEvent);
		assertThat(result,is(equalTo(Message.NULL)));
		assertThat("Incorrect sequence of events",executionFinished && ! commit && ! executionStart);
		result = b.process(commitEvent);
		assertThat(result,is(equalTo(Message.NULL)));
		assertThat("Incorrect sequence of events",executionFinished && commit && ! executionStart);
		result = b.process(execStartEvent);
		assertThat(result,is(equalTo(Message.NULL)));
		assertThat("Incorrect sequence of events",executionFinished && commit &&  executionStart);
	}
	
	@Test
	public void should_returnNullMessageOnDO_NOTHING(){
		assertThat(b.process(suiteFinish),is(equalTo(Message.NULL)));
	}
	
	@Test
	public void should_returnOriginalMessageOnPASS_TROUGH(){
		assertThat(b.process(suiteStart).toEvent(),is(equalTo(suiteStart)));		
	}

	@Test
	public void should_useTypeToRecognizeEvent(){
		assertThat(b.process(log).toEvent(), is(equalTo(log)));
	}
	
	@Test
	public void should_useTruePredicateOnUnspecifiedEvents(){
		assertThat(b.process(action).toEvent(), is(equalTo(action)));
	}
	
	
	@Test
	public void should_returnNullMessageOnDefaultSelector(){
		assertThat(SelectiveActionBuilder.newRule().process(action), is(equalTo(Message.NULL)));	
	}
	
	@Test
	public void should_keepOrderOfActions(){
		SelectiveActionBuilder simple = SelectiveActionBuilder.newRule()
				.on(EventType.ACTION).run(Action.DO_NOTHING)
				.on(EventType.ALL).run(Action.PASS_TROUGH);
		assertThat(simple.process(action), is(equalTo(Message.NULL)));
		assertThat(simple.process(log).toEvent(), is(equalTo(log)));
	}

}
