/**
 * 
 */
package org.activejpa.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author ganeshs
 *
 */
public interface EntityManagerProvider {

	EntityManagerFactory getEntityManagerFactory();
	
	EntityManager getEntityManager();
}
