package com.automationrockstars.design.gir.screenplay;

public interface Task {

	<T extends Actor> void  performAs(T actor);
}
