/**
 * 
 */
package org.activejpa.enhancer;

import java.lang.management.ManagementFactory;
import java.security.CodeSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaAgentLoader {

	private static final Logger logger = LoggerFactory.getLogger(ActiveJpaAgentLoader.class);
	
	private static final ActiveJpaAgentLoader loader = new ActiveJpaAgentLoader();
	
	private ActiveJpaAgentLoader() {
	}
	
	public static ActiveJpaAgentLoader instance() {
		return loader;
	}

    public void loadAgent() {
        logger.info("dynamically loading javaagent");
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
