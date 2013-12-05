/**
 * 
 */
package org.activejpa.examples.petclinic.util;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.activejpa.jpa.JPA;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author ganeshs
 *
 */
public class CustomContextListener extends ContextLoaderListener {
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			ActiveJpaAgentLoader.instance().loadAgent();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		super.contextInitialized(event);
		JPA.instance.addPersistenceUnit("default", getCurrentWebApplicationContext().getBean(EntityManagerFactory.class), true);
	}

}
