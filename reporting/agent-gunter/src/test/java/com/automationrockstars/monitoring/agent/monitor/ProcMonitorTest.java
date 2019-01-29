package com.automationrockstars.monitoring.agent.monitor;


import com.automationrockstars.monitoring.agent.SigarHolder;
import com.automationrockstars.monitoring.agent.process.ProcUtil;
import com.google.common.collect.Lists;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.automationrockstars.asserts.Asserts.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({ProcUtil.class, SigarHolder.class})
@RunWith(PowerMockRunner.class)
public class ProcMonitorTest {

    private static final String PROCESS_ARGS = "/u01/WASTEST4/WebSphere/8.5/java_1.7_64/bin/java -Xmaxt0.5 -Dwas.status.socket=37244 -Declipse.security -Dosgi.install.area=/u01/WASTEST4/WebSphere/8.5 -Dosgi.configuration.area=/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/servers/nodeagent/configuration -Dosgi.framework.extensions=com.ibm.cds,com.ibm.ws.eclipse.adaptors -Xshareclasses:name=webspherev85_1.7_64_%g,nonFatal -Dcom.ibm.xtq.processor.overrideSecureProcessing=true -Xcheck:dump -Xbootclasspath/p:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ibmorb.jar -Dorg.osgi.framework.bootdelegation=* -classpath /u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/properties:/u01/WASTEST4/WebSphere/8.5/properties:/u01/WASTEST4/WebSphere/8.5/lib/startup.jar:/u01/WASTEST4/WebSphere/8.5/lib/bootstrap.jar:/u01/WASTEST4/WebSphere/8.5/lib/jsf-nls.jar:/u01/WASTEST4/WebSphere/8.5/lib/lmproxy.jar:/u01/WASTEST4/WebSphere/8.5/lib/urlprotocols.jar:/u01/WASTEST4/WebSphere/8.5/deploytool/itp/batchboot.jar:/u01/WASTEST4/WebSphere/8.5/deploytool/itp/batch2.jar:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/lib/tools.jar -Dorg.osgi.framework.bootdelegation=* -Dibm.websphere.internalClassAccessMode=allow -Xms50m -Xmx256m -Xcompressedrefs -Xscmaxaot4M -Xnoaot -Xscmx90M -Dws.ext.dirs=/u01/WASTEST4/WebSphere/8.5/java_1.7_64/lib:/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/classes:/u01/WASTEST4/WebSphere/8.5/classes:/u01/WASTEST4/WebSphere/8.5/lib:/u01/WASTEST4/WebSphere/8.5/installedChannels:/u01/WASTEST4/WebSphere/8.5/lib/ext:/u01/WASTEST4/WebSphere/8.5/web/help:/u01/WASTEST4/WebSphere/8.5/deploytool/itp/plugins/com.ibm.etools.ejbdeploy/runtime -Dderby.system.home=/u01/WASTEST4/WebSphere/8.5/derby -Dcom.ibm.itp.location=/u01/WASTEST4/WebSphere/8.5/bin -Djava.util.logging.configureByServer=true -Duser.install.root=/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent -Djava.ext.dirs=/u01/WASTEST4/WebSphere/8.5/tivoli/tam:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ext -Djavax.management.builder.initial=com.ibm.ws.management.PlatformMBeanServerBuilder -Dpython.cachedir=/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/temp/cachedir -Dwas.install.root=/u01/WASTEST4/WebSphere/8.5 -Djava.util.logging.manager=com.ibm.ws.bootstrap.WsLogManager -Dserver.root=/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent -Dcom.ibm.security.jgss.debug=off -Dcom.ibm.security.krb5.Krb5Debug=off -Djava.awt.headless=true -Djava.library.path=/u01/WASTEST4/WebSphere/8.5/lib/native/aix/ppc_64/:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ppc64/compressedrefs:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ppc64:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ppc64:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ppc64/compressedrefs:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ppc64/j9vm:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/ppc64:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/../lib/ppc64:/u01/WASTEST4/WebSphere/8.5/bin:/u01/WASTEST4/WebSphere/8.5/nulldllsdir:/usr/lib:/usr/lib: -Djava.endorsed.dirs=/u01/WASTEST4/WebSphere/8.5/endorsed_apis:/u01/WASTEST4/WebSphere/8.5/java_1.7_64/jre/lib/endorsed -Djava.security.auth.login.config=/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/properties/wsjaas.conf -Djava.security.policy=/u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/properties/server.policy com.ibm.wsspi.bootstrap.WSPreLauncher -nosplash-application com.ibm.ws.bootstrap.WSLauncher com.ibm.ws.runtime.WsServer /u01/WASTEST4/WebSphere/8.5/profiles/WASTEST4_iehibu62_Nodeagent/config WASTEST4_Cell WASTEST4_iehibu62_Nodeagent nodeagent";


    @Test
    public void should_addAndRemoveProcesses() throws SigarException {
        mockStatic(ProcUtil.class);
        mockStatic(SigarHolder.class);
        SigarProxy sigar = mock(SigarProxy.class);
        when(sigar.getProcCpu(anyLong())).thenReturn(new ProcCpu());
        when(sigar.getProcMem(anyLong())).thenReturn(new ProcMem());
        when(SigarHolder.getSigar()).thenReturn(sigar);
        when(ProcUtil.getPID(anyString())).thenReturn(Lists.newArrayList(1L));

        DynamicProcessMonitor monitor = new DynamicProcessMonitor("mock");
        int singleProcess = monitor.sample().size();
        when(ProcUtil.getPID(anyString())).thenReturn(Lists.newArrayList(1L, 2L));
        assertThat(monitor.sample().size(), is(equalTo(singleProcess * 2)));
        when(ProcUtil.getPID(anyString())).thenReturn(Lists.newArrayList(1L));
        assertThat(monitor.sample().size(), is(equalTo(singleProcess)));
        monitor.close();
    }

    @Test
    public void should_getDynamicFromProccessArgs() throws SigarException {
        mockStatic(ProcUtil.class);
        mockStatic(SigarHolder.class);
        SigarProxy sigar = mock(SigarProxy.class);
        when(sigar.getProcCpu(anyLong())).thenReturn(new ProcCpu());
        when(sigar.getProcMem(anyLong())).thenReturn(new ProcMem());
        when(SigarHolder.getSigar()).thenReturn(sigar);
        when(ProcUtil.getPID(anyString())).thenReturn(Lists.newArrayList(1L));
        when(ProcUtil.getProcArgs(anyLong())).thenReturn(PROCESS_ARGS);
        DynamicProcessMonitor monitor = new DynamicProcessMonitor("WebSph", "config WASTEST4.*$");
        assertThat(monitor.sample().entrySet().iterator().next().getKey(),
                startsWith("config WASTEST4_Cell WASTEST4_iehibu62_Nodeagent nodeagent".replaceAll("\\W", "_")));
        monitor.close();
    }

}
