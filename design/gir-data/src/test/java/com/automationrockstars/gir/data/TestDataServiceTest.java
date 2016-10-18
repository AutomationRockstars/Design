package com.automationrockstars.gir.data;
import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class TestDataServiceTest {
	private static final AtomicInteger order = new AtomicInteger(0);
	public static interface Persona extends TestDataRecord {

		String name();
		
		int size();
		
		Date birth();
		
		String getSurname();
	}
	
	
	@Test(timeout=60000)
	public void should_addAndTakeTestData() throws IOException {
		final TestData<Persona> people = TestDataServices.testData(Persona.class);	
		
		ExecutorService allTheVusers = Executors.newFixedThreadPool(300);
		final Random sleeper = new Random(231112131);
		
		Runnable addRecord = new Runnable() {
			
			@Override
			public void run() {
				
				try {
					Thread.sleep(sleeper.nextInt(10));
				} catch (InterruptedException ignore) {
				}
				people.addNew()
				.with("name", "John No'" + System.nanoTime())
				.with("surname","von Avivovitch")
				.with("size", order.getAndIncrement())
				.with("birth",new Date());
			}
		};
		
		for (int i =0;i<2001;i++){
			allTheVusers.submit(addRecord);
		}
		while (people.records().size() < 1){
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) {
			}
		}
		while (people.records().size() < 2000){
			assertThat(people.records().last().get(),is(not(nullValue())));
			
		}
		
		allTheVusers.shutdownNow();
	}
}
