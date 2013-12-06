/**
 * 
 */
package org.activejpa.enhancer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class loader that emulates the functionality of the agent. To be used for testing in an environment where loading agent is not possible
 * 
 * WARNING: Use it only for testing. Not recommended for production use. 
 * 
 * @author ganeshs
 *
 */
public class MyClassLoader extends ClassLoader {
	
	private DomainClassEnhancer enhancer;
	
	private boolean initialzed;
	
	private List<String> excludedPackages = new ArrayList<String>(Arrays.asList("java.", "javax.", "sun.", "org.mockito"));
	
	private Logger logger = LoggerFactory.getLogger(MyClassLoader.class);
	
	public MyClassLoader() {
		this(Thread.currentThread().getContextClassLoader());
	}
	
	public MyClassLoader(ClassLoader loader) {
		this(loader, null);
	}
	
	public MyClassLoader(ClassLoader loader, List<String> excludedPackages) {
		super(loader);
		if (excludedPackages != null) {
			this.excludedPackages.addAll(excludedPackages);
		}
	}
	
	private void init() {
		enhancer = new DomainClassEnhancer();
		initialzed = true;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (! initialzed) {
			init();
		}
		logger.trace("Loading the class " + name);
		for (String excluded : excludedPackages) {
			if (name.startsWith(excluded)) {
				return super.loadClass(name, resolve);
			}
		}
		Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			logger.trace("Class " + name + " is already loaded");
			return clazz;
		}
		
		byte[] bytes = null;
		if (enhancer.canEnhance(name)) {
			bytes = enhancer.enhance(this, name);
		} else {
			InputStream classData = getResourceAsStream(name.replace('.', '/') + ".class");
			if(classData == null) {
	            return super.loadClass(name, resolve);
	        }
		
			try {
				bytes = toByteArray(classData);   
			} catch (Exception e) {
				logger.debug("Failed while converting classdata to bytes for the class " + name, e);
				return super.loadClass(name, resolve);
			} finally {
				if (classData != null) {
					try {
						classData.close();
					} catch (IOException e) {
						// Ignore. Can't do much
						logger.trace("Failed while closing the classData for the class " + name, e);
					}
				}
			}
		}
		
		clazz = defineClass(name, bytes, 0, bytes.length);
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}
	
	
	private static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] bytes = new byte[4096];
        int n = 0;
        while (-1 != (n = is.read(bytes))) {
            os.write(bytes, 0, n);
        }
        return os.toByteArray();
	}
}
