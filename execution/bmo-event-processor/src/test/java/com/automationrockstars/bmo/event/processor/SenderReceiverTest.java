package com.automationrockstars.bmo.event.processor;

import org.junit.Test;

import com.automationrockstars.bmo.event.processor.internal.RuleReporter;
import com.google.common.base.Predicate;

import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.*;

import java.lang.reflect.Method;
public class SenderReceiverTest {

	@Test
	public void detectRules(){
		assertThat(RuleReporter.validRules().size(), is(greaterThan(0)));
		assertThat("Good rules only",RuleReporter.validRules().allMatch(new Predicate<Method>() {

			@Override
			public boolean apply(Method input) {
				return input.getName().contains("good");
			}
		}));
	}
}
