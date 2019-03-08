package com.automationrockstars.monitoring.agent.monitor;

import com.automationrockstars.monitoring.agent.process.ProcUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.automationrockstars.monitoring.agent.SigarHolder.getSigar;

public class DynamicProcessMonitor extends OsMonitor {

    private final String processFilter;
    private final Pattern nameFilter;
    private final Map<Long, String> processMap = Maps.newHashMap();

    public DynamicProcessMonitor(String processFilter, String nameFilter) {
        this.processFilter = processFilter;
        this.nameFilter = Pattern.compile(nameFilter);
    }

    public DynamicProcessMonitor(String processFilter) {
        this.processFilter = processFilter;
        this.nameFilter = null;
    }

    private Map<Long, String> processes() {
        if (processMap.isEmpty()) {
            ProcUtil.saveProcesses(processFilter);
        }
        final List<Long> currentProcesses = ProcUtil.getPID(processFilter);
        for (Long newPid : currentProcesses) {
            if (!processMap.containsKey(newPid)) {

                try {
                    Files.append(new Date() + ": " + name(newPid) + "\n", Paths.get("processes").toFile(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                processMap.put(newPid, name(newPid));
            }
        }
        Iterables.removeIf(processMap.keySet(), new Predicate<Long>() {
            @Override
            public boolean apply(Long input) {
                return !currentProcesses.contains(input);
            }
        });

        return processMap;
    }

    private String name(Long process) {
        String namePostfix = "PID_" + String.valueOf(process);
        if (nameFilter != null) {
            Matcher m = nameFilter.matcher(ProcUtil.getProcArgs(process));
            if (m.find()) {
                namePostfix = m.group();
            }
        }
        return namePostfix;
    }

    @Override
    public Map<String, Number> sample() {
        final Map<String, Number> result = Maps.newHashMap();
        for (Entry<Long, String> process : processes().entrySet()) {
            try {
                result.putAll(convert(getSigar().getProcCpu(process.getKey()).toMap(), process.getValue() + "_cpu_"));
            } catch (SigarException ignore) {
            }
            try {
                result.putAll(convert(getSigar().getProcMem(process.getKey()).toMap(), process.getValue() + "_mem_"));
            } catch (SigarException ignore) {
            }
        }
        return result;
    }

    @Override
    public String name() {
        return "prc";
    }

}
