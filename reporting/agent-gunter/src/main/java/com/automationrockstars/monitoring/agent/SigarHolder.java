package com.automationrockstars.monitoring.agent;

import java.nio.file.Paths;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarProxy;

import com.automationrockstars.base.JarUtils;

public class SigarHolder {
	private static Sigar sigar;

	public static synchronized SigarProxy getSigar(){
		if (sigar == null){
			JarUtils.unzipDirectory(Paths.get("lib"), "sigar/lib");
			String initialLib = System.getProperty("java.library.path", "");
			initialLib = initialLib + System.getProperty("path.separator") + Paths.get("lib").toAbsolutePath().toString();
			System.setProperty("java.library.path", initialLib);
			sigar = new Sigar();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					closeSigar();					
				}
			}));
		}
		return sigar;
	}

	public static synchronized void closeSigar(){
		if (sigar != null){
			sigar.close();
		}
		sigar = null;
	}

}
