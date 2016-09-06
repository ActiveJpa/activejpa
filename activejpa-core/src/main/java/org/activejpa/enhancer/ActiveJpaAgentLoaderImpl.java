/**
 * 
 */
package org.activejpa.enhancer;

import java.lang.management.ManagementFactory;
import java.security.CodeSource;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaAgentLoaderImpl {
	
	public static void loadAgent() {
		String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            CodeSource codeSource = ActiveJpaAgent.class.getProtectionDomain().getCodeSource();
            vm.loadAgent(codeSource.getLocation().toURI().getPath(), "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
