/**
 * 
 */
package org.activejpa.enhancer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ganeshs
 *
 */
public class MyClassLoader extends ClassLoader {
	
	private DomainClassEnhancer enhancer;
	
	private boolean initialzed;
	
	public MyClassLoader() {
		this(Thread.currentThread().getContextClassLoader());
	}
	
	public MyClassLoader(ClassLoader loader) {
		super(loader);
	}
	
	private void init() {
		enhancer = new DomainClassEnhancer();
		try {
			loadClass("org.activejpa.entity.EntityModel", true);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			initialzed = true;
		}
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (! initialzed) {
			init();
		}
		System.out.println("Loading the class " + name);
		if (name.startsWith("java.") || name.startsWith("javax.")) {
			return super.loadClass(name, resolve);
		}
		Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			System.out.println("Class " + name + " is already loaded");
			return clazz;
		}
		
		InputStream classData = getResourceAsStream(name.replace('.', '/') + ".class");
		byte[] bytes = null;
		try {
	        if(classData == null) {
	        	System.out.println("Loading from parent loader");
	            return super.loadClass(name, resolve);
	        }
	        bytes = toByteArray(classData);
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (classData != null) {
				try {
					classData.close();
				} catch (IOException e) {
					// Ignore. Can't do much
				}
			}
		}
		
		if (bytes == null) {
			return super.loadClass(name, resolve);
		}
        
		if (enhancer.canEnhance(name)) {
			bytes = enhancer.enhance(this, name);
		}
		clazz = defineClass(name, bytes, 0, bytes.length);
		System.out.println("Loaded class " + clazz.getClassLoader());
		if (resolve) {
			resolveClass(clazz);
		}
		return super.loadClass(name, resolve);
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
