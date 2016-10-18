package com.example.jvm;

import java.io.IOException;

import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Test;


public class Jmx {

	@Test
	public void doRun() throws IOException{
		System.setProperty("com.ibm.CORBA.ConfigURL", "file:///tools/was/runtimes/properties/sas.client.props");
			
		JMXServiceURL url = new JMXServiceURL("service:jmx:iiop://server/jndi/JMXConnector");
		
		try {
			final JMXConnector cnt = JMXConnectorFactory.connect(url);
		
//		FluentIterable<ObjectName> mbeans = FluentIterable.from(cnt.getMBeanServerConnection().queryNames(null, null));
//		mbeans.filter(new Predicate<ObjectName>() {
//
//			@Override
//			public boolean apply(ObjectName input) {
//				return ! input.getCanonicalName().contains("jsp");
//			}
//		}).forEach(new Consumer<ObjectName>() {
//
//			@Override
//			public void accept(ObjectName t) {
//				System.out.println(t.getCanonicalName()+ "\n" +
//				Joiner.on("\n\t").withKeyValueSeparator(":").join(t.getKeyPropertyList()));
//			}
//		});
//		
//		
		String query = "WebSphere:type=JVM,*";
        ObjectName queryName = new ObjectName(query);
//        
//        FluentIterable.from(cnt.getMBeanServerConnection().queryNames(queryName, null)).forEach(new Consumer<ObjectName>() {
//
//			@Override
//			public void accept(ObjectName t) {
//				
//				try {
//					System.out.println(Joiner.on("\n").join(cnt.getMBeanServerConnection().getMBeanInfo(t).getAttributes()));
//					System.out.println("=");
//					for (int i=0;i<30;i++){
//					System.out.println(cnt.getMBeanServerConnection().getAttribute(t, "maxMemory"));
//					System.out.println(cnt.getMBeanServerConnection().getAttribute(t, "freeMemory"));
//					System.out.println(cnt.getMBeanServerConnection().getAttribute(t, "heapSize"));
//					System.out.println(cnt.getMBeanServerConnection().getAttribute(t, "stats"));
//					Thread.sleep(10000);
//					}
//					
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
        
        if (cnt != null)
			cnt.close();
		} catch (Throwable t){
			
			t.printStackTrace();
		} finally {
				
		}
	}
	
}
