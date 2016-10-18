package com.automationrockstars.monitoring.agent.monitor;

import static com.automationrockstars.monitoring.agent.SigarHolder.getSigar;

import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.SigarException;
public class CpuMonitor extends OsMonitor {


	@Override
	public Map<String, Number> sample() {
		final Map<String, Number> result = new HashMap<String, Number>();
		try {
			result.putAll(convert(getSigar().getCpu().toMap(),""));	
			result.put("IdlePercentage", getSigar().getCpuPerc().getIdle());
			result.put("CombinedPercentage", getSigar().getCpuPerc().getCombined());

		} catch (SigarException ignore) {

		}
		return result;
	}

	@Override
	public String name() {
		return "cpu";
	}

}
