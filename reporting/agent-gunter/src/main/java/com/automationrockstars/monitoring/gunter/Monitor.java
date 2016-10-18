package com.automationrockstars.monitoring.gunter;

import java.io.Closeable;
import java.util.Map;

/**
 *  
 *
 */
public interface Monitor extends Closeable {

   
    Map<String, Number> sample();

    
    String name();
}
