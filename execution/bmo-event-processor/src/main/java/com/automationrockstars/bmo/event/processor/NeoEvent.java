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

package com.automationrockstars.bmo.event.processor;

import com.automationrockstars.gunter.EventType;
import com.automationrockstars.gunter.events.Event;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NeoEvent {

    public static final String ID = "id";
    public static final String PARENT_ID = "parentId";
    public static final String TIMESTAMP = "time";
    private final String type;
    private Map<String, Object> attributes = Maps.newHashMap();

    public NeoEvent(EventType type) {
        this(type.name());
    }

    public NeoEvent(String type) {
        this.type = type;
    }

    private static String timeStamp(Object ts) {
        if (ts instanceof Date) {
            return new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.SSS").format(ts);
        }
        if (ts instanceof Double) {
            return timeStamp(((Double) ts).longValue());
        } else if (ts instanceof Long) {
            return timeStamp(new Date((Long) ts));
        } else {
            return timeStamp(Long.valueOf((String) ts));
        }

    }

    public static NeoEvent fromJson(String json) {
        return fromMap(EventType.COMMIT, new Gson().fromJson(json, HashMap.class));
    }

    public static NeoEvent fromMap(EventType type, Map<String, Object> attrs) {
        NeoEvent result = new NeoEvent(type.name());
        result.setAttributes(attrs);
        return result;
    }

    private static String attributes(Map<String, Object> attributes) {
        StringBuilder result = new StringBuilder();
        for (Entry<String, Object> attr : attributes.entrySet()) {
            if ((!attr.getKey().equals("attributes")) && (!attr.getKey().equals("type")) && null != attr.getValue()) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                if (Map.class.isAssignableFrom(attr.getValue().getClass())) {
                    result.append(attributes((Map<String, Object>) attr.getValue()));
                } else {
                    result.append(attr.getKey()).append(":");
                    if (attr.getKey().equals("timeStamp")) {
                        result.append("\"").append(timeStamp(attr.getValue())).append("\"");
                    } else if (Number.class.isAssignableFrom(attr.getValue().getClass())) {
                        result.append(attr.getValue());
                    } else if (attr.getValue() instanceof Boolean) {
                        result.append(attr.getValue());
                    } else result.append("\"").append(attr.getValue()).append("\"");
                }
            }
        }
        return result.toString();
    }

    public static NeoEvent fromEvent(Message eventToStore) {
        Event event = eventToStore.toEvent();
        NeoEvent result = new NeoEvent(event.getType());
        result.setId(event.getId());
        result.setParentId(event.getParentId());
        result.setTimeStamp(event.getTimeStamp());
        result.setAttributes(event.getAttributes());
        return result;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }

    public void setTimeStamp(Object ts) {
        attributes.put(TIMESTAMP, timeStamp(ts));
    }

    public String getParentId() {
        return (String) attributes.get(PARENT_ID);
    }

    public void setParentId(String parentId) {
        attributes.put(PARENT_ID, parentId);
    }

    public String getId() {
        return (String) attributes.get(ID);
    }

    public void setId(String id) {
        attributes.put(ID, id);
    }

    public String toString() {
        return String.format("(n:%s {%s})", type, attributes(attributes));
    }

    public boolean hasParent() {
        return attributes.get(PARENT_ID) != null;
    }

    public String createCommand() {
        return String.format("CREATE %s", this.toString());
    }

    public String createCommandWithParent() {
        StringBuilder command = new StringBuilder();
        if (hasParent()) {
            command.append("MATCH (parent {id:\"")
                    .append(getParentId()).append("\"}\n")
                    .append("CREATE (parent)<-[:PARENT]-")
                    .append(toString());
        } else {
            command.append(createCommand());
        }
        return command.toString();
    }

}
