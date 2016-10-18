package com.automationrockstars.bmo.event.processor;

import com.automationrockstars.gunter.events.Event;
import static com.automationrockstars.bmo.event.processor.Message.Builder.*;
public interface Action<T extends Event> {

	Message process(T event);
	
	public static final Action<Event> DO_NOTHING = new Action<Event>(){

		@Override
		public Message process(Event event) {
			return null;
		}		
	};
	
	public static final Action<Event> PASS_TROUGH = new Action<Event>(){

		@Override
		public Message process(Event event) {
			return newMessage().withEvent(event);
		}
		
	};
	
	public static final Action<Event> STORE = new Action<Event>(){
		
		@Override
		public Message process(Event event){
			EventStorage.storage().storeIfParentStored(event);
			return null;
		}
	};
}
