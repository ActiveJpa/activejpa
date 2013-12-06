/**
 * 
 */
package org.activejpa.entity.testng;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import org.testng.IObjectFactory;

/**
 * @author ganeshs
 *
 */
public class DomainClassObjectFactory implements IObjectFactory {
	
	private static final long serialVersionUID = 1L;
	
	private ClassLoader loader;
	
	public DomainClassObjectFactory(List<String> ingnoredPackages) throws Exception {
		Class<?> clazz = Class.forName("org.activejpa.enhancer.MyClassLoader");
		Constructor<?> constructor = clazz.getConstructor(ClassLoader.class, List.class);
		loader = (ClassLoader) constructor.newInstance(Thread.currentThread().getContextClassLoader(), ingnoredPackages);
		Thread.currentThread().setContextClassLoader(loader);
	}
	
	public DomainClassObjectFactory() throws Exception {
		this(Arrays.asList("org.xml."));
	}

	@SuppressWarnings("rawtypes")
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
