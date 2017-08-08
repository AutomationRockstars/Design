package com.automationrockstars.design.gir.screenplay;

public interface Question {

	<T extends Actor> void askAs(T actor) throws AssertionError;
}
