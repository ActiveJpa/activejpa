/**
 * 
 */
package org.activejpa.enhancer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        ClassLoader loader = null;
		try {
	    	loader = getClassLoader();
	    	loader.loadClass("org.activejpa.enhancer.ActiveJpaAgentLoaderImpl").getMethod("loadAgent").invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    private ClassLoader getClassLoader() throws Exception {
    	File javaHome = new File(System.getProperty("java.home"));
        String toolsPath = javaHome.getName().equalsIgnoreCase("jre") ? "../lib/tools.jar" : "lib/tools.jar";
        URL[] urls = new URL[] {
    			ActiveJpaAgent.class.getProtectionDomain().getCodeSource().getLocation(),
    			new File(javaHome, toolsPath).getCanonicalFile().toURI().toURL()
    	};
        return new URLClassLoader(urls, null);
    }
}
