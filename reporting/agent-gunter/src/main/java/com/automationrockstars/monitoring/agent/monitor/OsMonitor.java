package com.automationrockstars.monitoring.agent.monitor;

import com.automationrockstars.monitoring.gunter.Monitor;

import java.util.HashMap;
import java.util.Map;

public abstract class OsMonitor implements Monitor {


    protected Map<String, Number> convert(
            final Map<?, ?> input, String keyPrefix) {
        final Map<String, Number> convertedResult = new HashMap<>();
        for (Object key : input.keySet()) {
            convertedResult.put((keyPrefix + (String) key).replaceAll("\\W", "_").replaceAll("_+", "_"), (Number) Double.valueOf((String) input.get(key)));
        }
        return convertedResult;
    }

    @Override
    public void close() {

    }


}
