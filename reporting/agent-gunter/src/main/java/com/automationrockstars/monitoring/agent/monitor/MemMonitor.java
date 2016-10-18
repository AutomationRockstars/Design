package com.automationrockstars.monitoring.agent.monitor;

import static com.automationrockstars.monitoring.agent.SigarHolder.getSigar;

import java.util.Map;

import org.hyperic.sigar.SigarException;
public class MemMonitor extends OsMonitor  {

	@Override
	public Map<String, Number> sample() {
		try {
			return convert(getSigar().getMem().toMap(),"");
		} catch (SigarException e) {
			return null;
		}
	}


	@Override
	public String name() {
		return "mem";
	}
}
