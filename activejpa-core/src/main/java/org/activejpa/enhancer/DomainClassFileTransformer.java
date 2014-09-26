/**
 * 
 */
package org.activejpa.enhancer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class DomainClassFileTransformer implements ClassFileTransformer {
	
	private DomainClassEnhancer enhancer;
	
	public static final List<String> ignoredPackages = Arrays.asList("javax/", "java/", "sun/", "com/sun/");
	
    private static final Logger logger = LoggerFactory.getLogger(DomainClassFileTransformer.class);
    
    public DomainClassFileTransformer(DomainClassEnhancer enhancer) {
    	this.enhancer = enhancer;
	}
    
    public DomainClassFileTransformer() {
    	this(new DomainClassEnhancer());
	}
    
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, 
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (skipClass(className)) {
			return null;
		}
		try {
			logger.trace("Trying to transform - " + className);
			if (loader == null) {
				loader = Thread.currentThread().getContextClassLoader();
			}
			return enhancer.enhance(loader, className);
		} catch (Exception e) {
			logger.error("Failed while transforming the class " + className, e);
			throw new IllegalClassFormatException(e.getMessage());
		}
	}
	
	/**
	 * Checks if the class can be ignored
	 * 
	 * @param className
	 * @return
	 */
	protected boolean skipClass(String className) {
		for (String name : ignoredPackages) {
			if (className.startsWith(name)) {
				return true;
			}
		}
		return false;
	}
}
