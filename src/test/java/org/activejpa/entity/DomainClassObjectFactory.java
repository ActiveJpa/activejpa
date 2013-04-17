/**
 * 
 */
package org.activejpa.entity;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.activejpa.enhancer.MyClassLoader;
import org.testng.IObjectFactory;

/**
 * @author ganeshs
 *
 */
public class DomainClassObjectFactory implements IObjectFactory {
	
	private MyClassLoader loader;
	
	public DomainClassObjectFactory() {
		 loader = new MyClassLoader(Thread.currentThread().getContextClassLoader(), Arrays.asList("org.xml."));
		 Thread.currentThread().setContextClassLoader(loader);
	}

	@Override
	public Object newInstance(Constructor constructor, Object... params) {
		Class<?> clazz = constructor.getDeclaringClass();
		try {
			clazz = loader.loadClass(clazz.getName());
			constructor = clazz.getConstructor(constructor.getParameterTypes());
			return constructor.newInstance(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
