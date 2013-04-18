/**
 * 
 */
package org.activejpa.jpa;

import javax.persistence.EntityManager;

/**
 * @author ganeshs
 *
 */
public final class JPAContext {

	private final JPAConfig config;
	
	private final boolean readOnly;
	
	private EntityManager entityManager;
	
	public JPAContext(JPAConfig jpaConfig, boolean readOnly) {
		this.config = jpaConfig;
		this.readOnly = readOnly;
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = config.getEntityManagerFactory().createEntityManager();
			if (readOnly) {
				entityManager.setProperty("readOnly", readOnly);
			}
		}
		return entityManager;
	}
	
	public void close() {
		entityManager.close();
	}
	
	public void beginTxn() {
		if (isTxnOpen()) {
			return;
		}
		getEntityManager().getTransaction().begin();
	}
	
	public void closeTxn(boolean rollback) {
		try {
			if (isTxnOpen()) {
				if (rollback || readOnly || entityManager.getTransaction().getRollbackOnly()) {
					entityManager.getTransaction().rollback();
				} else {
					entityManager.getTransaction().commit();
				}
			}
		} finally {
			config.clearContext();
			close();
		}
	}
	
	public boolean isTxnOpen() {
		return entityManager != null && entityManager.getTransaction() != null && entityManager.getTransaction().isActive();
	}

	/**
	 * @return the readOnly
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
}
