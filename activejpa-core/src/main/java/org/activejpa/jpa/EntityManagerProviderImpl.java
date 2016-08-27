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
public class EntityManagerProviderImpl implements EntityManagerProvider {
	
	private EntityManagerFactory factory;
	
	/**
	 * @param factory
	 */
	public EntityManagerProviderImpl(EntityManagerFactory factory) {
		this.factory = factory;
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return factory;
	}

	@Override
	public EntityManager getEntityManager() {
		return factory.createEntityManager();
	}

}
