/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */

package com.automationrockstars.design.gir.screenplay;

import com.automationrockstars.base.ConfigLoader;
import com.automationrockstars.base.JarUtils;
import com.automationrockstars.gir.data.TestData;
import com.automationrockstars.gir.data.TestDataRecord;
import com.automationrockstars.gir.data.TestDataServices;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath.ResourceInfo;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.automationrockstars.gir.data.TestData.REMOTE_DATA_URL_PROP;


public class Actor {


    private static final ConcurrentMap<String, Actor> ACTOR_CACHE = Maps.newConcurrentMap();
    private static Logger LOG = LoggerFactory.getLogger(Actor.class);
    private static Set<String> availableActors = null;
    private final String name;
    private final AtomicInteger cycle = new AtomicInteger();

    private Actor(String name) {
        Preconditions.checkArgument(supportedActors().contains(name), "Actor %s is not defined. Defined actors: %s", name, supportedActors());
        this.name = name;
        TestDataServices.pool(name).loadFrom(name + ".json");

    }

    private static boolean isRemote(){
        return ConfigLoader.config().containsKey(REMOTE_DATA_URL_PROP);
    }
    public static Actor forName(String name) {
        Actor result = ACTOR_CACHE.get(name);
        if (result == null) {
            result = new Actor(name);
            ACTOR_CACHE.put(name, result);
        }
        return result;
    }


    private static void localSupportedActors(){
            String[] actorLocation = ConfigLoader.config().getStringArray("actor.location.parts");
            if (actorLocation == null || actorLocation.length < 1) {
                actorLocation = new String[]{"data", "actor", "json"};
            }
            availableActors.addAll(JarUtils.findResources(actorLocation).transform(new Function<ResourceInfo, String>() {
                @Override
                public String apply(ResourceInfo input) {
                    String[] nameParts = input.getResourceName().split("\\.|/");
                    return nameParts[nameParts.length - 2];
                }
            }).toList());
            LOG.info("Found following actors {}", availableActors);
        }



    private static void remoteSupportedActors(){

        Gson reader = new Gson();
        try {
            URL remoteActorList = new URL(ConfigLoader.config().getString(REMOTE_DATA_URL_PROP));
            LOG.info("Loading actor list from {}",remoteActorList);
            try (InputStreamReader remoteLoad = new InputStreamReader(remoteActorList.openStream())){
                List<String> actors = reader.fromJson(remoteLoad, ArrayList.class);
                availableActors.addAll(actors);
            }
        } catch (IOException e) {
            LOG.error("Actor list cannot be loaded from {}",ConfigLoader.config().getString(REMOTE_DATA_URL_PROP));
        }

    }

    public static List<String> supportedActors() {
        if (availableActors == null) {
            availableActors = Sets.newHashSet();
        }
        if (isRemote()){
            remoteSupportedActors();
        } else {
            localSupportedActors();
        }
        return Lists.newArrayList(availableActors);
    }

    public int nextCycle() {
        return cycle.incrementAndGet();
    }

    public int cycle() {
        return cycle.get();
    }

    public <T extends TestDataRecord> TestData<T> data(Class<T> type) {
        return TestDataServices.pool(name).testData(type);
    }

    public <T extends TestDataRecord> T currentRecordFor(Class<T> type) {
        if (TestDataServices.pool(name).testData(type).records().size() > cycle()) {
            return TestDataServices.pool(name).testData(type).record(cycle());
        } else {
            return null;
        }
    }

    public Actor perform(Task task) {
        LOG.info("{} is performing {}", name, task.getClass().getSimpleName());
        task.performAs(this);
        return this;
    }


    public Actor ask(Question question) throws AssertionError {
        LOG.info("{} is asking {}", name, question.getClass().getSimpleName());
        question.askAs(this);
        return this;
    }


}
