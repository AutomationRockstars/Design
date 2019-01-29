package com.automationrockstars.monitoring.agent.process;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.hyperic.sigar.SigarException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static com.automationrockstars.monitoring.agent.SigarHolder.getSigar;

public class ProcUtil {


    private static final ConcurrentMap<Long, String> imageCache = Maps.newConcurrentMap();
    private static File PROCESSES = Paths.get("processes").toFile();

    /**
     * @param processes
     * @return PID of specified process
     */
    public static synchronized List<Long> getPID(String processes) {
        final List<Long> processIndex = Lists.newArrayList();
        try {
            for (long processId : getSigar().getProcList()) {
                if (containsAll(processId, processes)) {
                    processIndex.add(processId);
                }
            }
        } catch (SigarException e) {
        }
        return processIndex;
    }

    public static void saveProcesses(String processes) {
        List<Long> processIndex = new ArrayList<Long>();
        try {
            try {
                FileUtils.forceDelete(PROCESSES);
            } catch (IOException e1) {
            }
            for (long processId : getSigar().getProcList()) {
                if (containsAll(processId, processes)) {
                    processIndex.add(processId);
                    try {
                        Files.append(String.format("%s \t %s \n", processId, getProcArgs(processId)), PROCESSES, Charset.defaultCharset());
                    } catch (IOException e) {

                    }
                }
            }
        } catch (SigarException e) {
        }


    }

    private static boolean containsAll(long processId, String processes) {
        boolean addIndex = true;
        for (String processName : getProcessDescription(processes)) {
            if (!includedInProcessArgs(processId, processName)) {
                addIndex = false;
            }
        }
        return addIndex;
    }

    private static boolean includedInProcessArgs(Long processId, String process) {
        if (imageCache.get(processId) == null) {
            imageCache.putIfAbsent(processId, getProcArgs(processId));
        }
        return imageCache.get(processId).contains(process);
    }

    public static String getProcArgs(long process) {
        String processArgs = "";
        try {
            for (String arg : getSigar().getProcArgs(process)) {
                processArgs += arg + " ";
            }
        } catch (SigarException e) {
        }
        return processArgs;
    }

    private static String[] getProcessDescription(String process) {
        return process.split(",");
    }


}
