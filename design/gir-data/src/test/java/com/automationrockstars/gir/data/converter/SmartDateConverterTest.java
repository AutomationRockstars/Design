package com.automationrockstars.gir.data.converter;

import java.util.Calendar;
import java.util.Date;
import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.*;
import static org.exparity.hamcrest.date.DateMatchers.*;

import org.exparity.hamcrest.date.DayMonthYear;
import org.junit.Test;

public class SmartDateConverterTest {

	@Test
	public void should_returnTodaysDate() {
		assertThat(new SmartDateConverter().convert(Date.class, "today"),isToday());
		assertThat(new SmartDateConverter().convert(Date.class, "now"),isToday());
	}
	
	@Test
	public void should_returnTomorrowDate() {
		assertThat(new SmartDateConverter().convert(Date.class, "tomorrow"),isTomorrow());
		assertThat(new SmartDateConverter().convert(Date.class, "in 1 day"),isTomorrow());		
	}
	
	@Test
	public void should_returnDateIn2Months(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 20);
		assertThat(new SmartDateConverter().convert(Date.class, "in 20 days"),is(sameDay(c.getTime())));
		c = Calendar.getInstance();
		c.add(Calendar.MONTH, 2);
		assertThat(new SmartDateConverter().convert(Date.class, "in 2 months"),is(sameMonth(c.getTime())));
	}
	
	

}
