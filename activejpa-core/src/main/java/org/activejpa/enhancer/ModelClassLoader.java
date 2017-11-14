/**
 * 
 */
package org.activejpa.enhancer;

import java.util.HashMap;

import net.bytebuddy.dynamic.loading.ByteArrayClassLoader.ChildFirst;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.PackageDefinitionStrategy;

/**
 * A class loader that emulates the functionality of the agent. To be used for testing in an environment where loading agent is not possible
 * 
 * WARNING: Use it only for testing. Not recommended for production use. 
 * 
 * @author ganeshs
 *
 */
public class ModelClassLoader extends ChildFirst {
    
    static {
        ByteBuddyAgent.install();
    }
	
    /**
     * Default Constructor
     */
    public ModelClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * @param loader
     */
    public ModelClassLoader(ClassLoader loader) {
        super(loader, new HashMap<>(), ClassLoadingStrategy.NO_PROTECTION_DOMAIN, PersistenceHandler.MANIFEST, PackageDefinitionStrategy.Trivial.INSTANCE, new ModelClassEnhancer().getTransformer());
    }
}
