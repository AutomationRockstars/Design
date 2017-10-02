package com.automationrockstars.gir.data.impl;

import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataPool;
import com.automationrockstars.gir.data.TestDataRecord;
import com.google.common.base.*;
import com.google.common.collect.*;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public class TestDataPermutatorImpl<T extends TestDataRecord> implements TestDataPool.TestDataPermutator<T> {

    public TestDataPermutatorImpl(TestDataPool pool){
        this.pool = pool;
    }

    private final TestDataPool pool;
    private List<Class<? extends TestDataRecord>> recordTypes = newArrayList();
    private boolean exclusive = false;
    private List<Predicate<T>> predicates = newArrayList();

    private TestData<T> populate(TestData<T> initial,Class<T> outputType){
        FluentIterable<Field> subRecords = FluentIterable.from(outputType.getFields());
        Map<Class<? extends TestDataRecord>,String> fieldMapping = Maps.newHashMap();
        for (Class<? extends TestDataRecord> recordType : recordTypes) {
            fieldMapping.put(recordType, subRecords.firstMatch(new Predicate<Field>() {
                @Override
                public boolean apply(@Nullable Field field) {
                    return field.getType().equals(recordType);
                }
            }).get().getName());
        }

        
        List<T> outputRecords = newArrayList();
        List<Set<? extends TestDataRecord>> prePermuter = newArrayList();
        for (Class<? extends TestDataRecord> recordType : recordTypes){
            prePermuter.add(pool.testData(recordType).records().toSet());

        }

        Set<List<TestDataRecord>> lists = Sets.cartesianProduct(prePermuter);
        for (List<TestDataRecord> combination : lists){
            Map<String,Object> outputRecord = Maps.newHashMap();
            for (TestDataRecord value : combination){
                outputRecord.put(fieldMapping.get(FluentIterable.from(recordTypes).firstMatch(input -> input.isAssignableFrom(value.getClass())).get()),value);
            }
            outputRecords.add(TestDataProxyFactory.create(outputRecord,outputType));
        }

        FluentIterable<T> filtered = FluentIterable.from(outputRecords);
        for (Predicate<T> predicate : predicates){
          filtered = filtered.filter(predicate);
        }
        filtered.stream().forEach(t -> initial.addNew().with(t));
        return initial;
    }
    @Override
    public TestData<T> build(Class<T> recordType) {
        return populate(pool.testData(recordType),recordType);
    }

    @Override
    public TestData<T> buildExclusive(Class<T> recordType) {
        return populate(pool.exclusiveTestData(recordType),recordType);
    }

    @Override
    public <V extends TestDataRecord> TestDataPool.TestDataPermutator<T> combine(Class<V>... recordTypes) {
        this.recordTypes.addAll(newArrayList(recordTypes));
        return this;
    }

    @Override
    public TestDataPool.TestDataPermutator<T> exclude(Predicate<T> predicate) {
        predicates.add(predicate);
        return this;
    }
}
