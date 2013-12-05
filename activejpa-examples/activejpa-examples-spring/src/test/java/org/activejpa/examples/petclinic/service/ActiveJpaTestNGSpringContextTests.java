/**
 * 
 */
package org.activejpa.examples.petclinic.service;

import java.lang.reflect.Method;

import javax.persistence.EntityManagerFactory;

import org.activejpa.jpa.JPA;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.internal.ObjectFactoryImpl;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaTestNGSpringContextTests extends AbstractTestNGSpringContextTests {

	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
		Class<?> clazz = Class.forName("org.activejpa.enhancer.ActiveJpaAgentLoader");
		Method method = clazz.getMethod("instance");
		Object loader = method.invoke(null);
		method = clazz.getMethod("loadAgent");
		method.invoke(loader);
		return new ObjectFactoryImpl();
	}
	
	@BeforeClass
	protected void beforeClass() {
		JPA.instance.addPersistenceUnit("test", applicationContext.getBean(EntityManagerFactory.class));
	}
}
