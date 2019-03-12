package com.automationrockstars.base.internal;

import com.google.common.collect.Maps;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ContextConfiguration extends AbstractConfiguration implements Cloneable {

    private static final Logger LOG = LoggerFactory.getLogger(ContextConfiguration.class);
    private static final ThreadLocal<ContextConfiguration> CONFIGS = InheritableThreadLocal.withInitial(() -> new ContextConfiguration(new MapConfiguration(Maps.newHashMap())));

    private final MapConfiguration inner;

    public ContextConfiguration(MapConfiguration inner) {
        this.inner=inner;
    }

    public static ContextConfiguration get(){
        LOG.trace("Returning config {} for thread {}",CONFIGS.get(),Thread.currentThread());
        return CONFIGS.get();
    }

    public static Configuration instance() {
        return get();
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        CONFIGS.get().inner.addProperty(key,value);
    }

    @Override
    public boolean isEmpty() {
        return CONFIGS.get().inner.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return CONFIGS.get().inner.containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return CONFIGS.get().inner.getProperty(key);
    }

    public void clearProperty(String key){
        CONFIGS.get().inner.clearProperty(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return CONFIGS.get().inner.getKeys();
    }

    public static void addContextProperty(String key, Object value){
        CONFIGS.get().addPropertyDirect(key,value);
    }
}
