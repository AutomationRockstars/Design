package com.automationrockstars.gir.data;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class TestDataRecordsIterable<E extends TestDataRecord> extends FluentIterable<E>{

	private final FluentIterable<E> innerIterable;
	private final AtomicInteger cursor = new AtomicInteger(0);
	private TestDataRecordsIterable(Iterable<E> iterable){
		this.innerIterable = FluentIterable.from(iterable);
	}
	public static <E extends TestDataRecord> TestDataRecordsIterable<E> fromA(Iterable<E> iterable){
		return new TestDataRecordsIterable<>(iterable);
	}

	@Override
	public Iterator<E> iterator() {
		return innerIterable.iterator();
	}
	
	public  Optional<E> next(){
		return Optional.of(innerIterable.get(cursor.getAndIncrement()));
	}
	
	public boolean hasNext(){
		return innerIterable.size() <= cursor.get();
	}
	

	public FluentIterable<E> get(){
		return innerIterable;
	}
	
	
}
