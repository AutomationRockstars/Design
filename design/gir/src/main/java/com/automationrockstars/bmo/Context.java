package com.automationrockstars.bmo;

import com.automationrockstars.base.ConfigLoader;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

public class Context {

    public static final String GENERAL = "GENERAL";
    public static final String STORY = "STORY";
    public static final String SCENARIO = "SCENARIO";
    public static final String TEST = "TEST";
    public static final String STEP = "STEP";

    private static final Map<String,ThreadLocal<Context>> contexts = Maps.newConcurrentMap();

    public static Context forName(String name){
        ThreadLocal<Context> ctx = contexts.get(name);
        if (ctx == null){
            ctx = ThreadLocal.withInitial(Context::new);
            contexts.put(name,ctx);
        }
        return ctx.get();
    }

    private MapConfiguration data = new MapConfiguration(Maps.newConcurrentMap());

    public Object get(String key){
        return data.getProperty(key);
    }

    public void put(String key, Object value){
        data.setProperty(key,value);
    }

    public<T> T get(String name, T defaultValue){
        Object value = data.getProperty(name);
        if (value == null){
            value = defaultValue;
        }
        return (T) value;
    }

    public void addProperty(String key, Object value) {
        data.addProperty(key, value);
    }

    public Configuration subset(String prefix) {
        return data.subset(prefix);
    }

    public void setProperty(String key, Object value) {
        data.setProperty(key, value);
    }

    public void clearProperty(String key) {
        data.clearProperty(key);
    }

    public void clear() {
        data.clear();
    }

    public Iterator<String> getKeys(String prefix) {
        return data.getKeys(prefix);
    }

    public Properties getProperties(String key) {
        return data.getProperties(key);
    }

    public Properties getProperties(String key, Properties defaults) {
        return data.getProperties(key, defaults);
    }

    public boolean getBoolean(String key) {
        return data.getBoolean(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return data.getBoolean(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return data.getBoolean(key, defaultValue);
    }

    public byte getByte(String key) {
        return data.getByte(key);
    }

    public byte getByte(String key, byte defaultValue) {
        return data.getByte(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue) {
        return data.getByte(key, defaultValue);
    }

    public double getDouble(String key) {
        return data.getDouble(key);
    }

    public double getDouble(String key, double defaultValue) {
        return data.getDouble(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        return data.getDouble(key, defaultValue);
    }

    public float getFloat(String key) {
        return data.getFloat(key);
    }

    public float getFloat(String key, float defaultValue) {
        return data.getFloat(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        return data.getFloat(key, defaultValue);
    }

    public int getInt(String key) {
        return data.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return data.getInt(key, defaultValue);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return data.getInteger(key, defaultValue);
    }

    public long getLong(String key) {
        return data.getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return data.getLong(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return data.getLong(key, defaultValue);
    }

    public short getShort(String key) {
        return data.getShort(key);
    }

    public short getShort(String key, short defaultValue) {
        return data.getShort(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue) {
        return data.getShort(key, defaultValue);
    }

    public BigDecimal getBigDecimal(String key) {
        return data.getBigDecimal(key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return data.getBigDecimal(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {
        return data.getBigInteger(key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return data.getBigInteger(key, defaultValue);
    }

    public String getString(String key) {
        return data.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return data.getString(key, defaultValue);
    }

    public String[] getStringArray(String key) {
        return data.getStringArray(key);
    }

    public List<Object> getList(String key) {
        return data.getList(key);
    }

    public List<Object> getList(String key, List<?> defaultValue) {
        return data.getList(key, defaultValue);
    }


}
