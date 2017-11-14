/**
 * 
 */
package org.activejpa.examples.petclinic.service;

import javax.persistence.EntityManagerFactory;

import org.activejpa.entity.testng.ActiveJpaAgentObjectFactory;
import org.activejpa.jpa.JPA;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;

/**
 * @author ganeshs
 *
 */
public class ActiveJpaTestNGSpringContextTests extends AbstractTestNGSpringContextTests {

	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
		return new ActiveJpaAgentObjectFactory();
	}
	
	@BeforeClass
	protected void beforeClass() {
		JPA.instance.addPersistenceUnit("test", applicationContext.getBean(EntityManagerFactory.class));
	}
}
