/**
 * 
 */
package org.activejpa.entity.testng;

import java.lang.reflect.Constructor;

import org.testng.IObjectFactory;

/**
 * Testng object factory 
 * 
 * @author ganeshs
 *
 */
public class ActiveJpaAgentObjectFactory implements IObjectFactory {
	
	private static final long serialVersionUID = 1L;
	
	private ClassLoader loader;
	
	/**
	 * @throws Exception
	 */
	public ActiveJpaAgentObjectFactory() throws Exception {
		Class<?> clazz = Class.forName("org.activejpa.enhancer.ModelClassLoader");
		Constructor<?> constructor = clazz.getConstructor(ClassLoader.class);
		loader = (ClassLoader) constructor.newInstance(Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
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
