package com.automationrockstars.monitoring.agent.monitor;



import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.automationrockstars.monitoring.gunter.Monitor;
import com.google.common.collect.Maps;

public class JMXMonitor implements Monitor {

	
	private  JMXConnector connector;
	private ObjectName bean;
	private List<MBeanAttributeInfo> attrs;
	@Override
	public Map<String, Number> sample() {
		Map<String,Number> result = Maps.newHashMap();
		for (MBeanAttributeInfo attr : attrs){
			try {
				Number value = (Number) connector.getMBeanServerConnection().getAttribute(bean, attr.getName());
				result.put(attr.getName(), value);
			} catch (AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException
					| IOException e) {
			}
			
		}
		return result;
	}

	public JMXMonitor(String url, String beanName) throws IOException, JMException {
		JMXServiceURL serviceUrl = new JMXServiceURL(url);

		try {
			connector = JMXConnectorFactory.connect(serviceUrl);
			bean = new ObjectName(beanName);
			MBeanAttributeInfo[] attrs = connector.getMBeanServerConnection().getMBeanInfo(bean).getAttributes();
			for (MBeanAttributeInfo attr : attrs){
				if (Number.class.isAssignableFrom(Class.forName(attr.getType()))){
					this.attrs.add(attr);
				}
			}
		} catch (Throwable t){

		}
	}


	@Override
	public void close() {
		if (connector != null){
			try {
				connector.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	@Override
	public String name() {

		return "JMX." + bean.toString();
	}


}
