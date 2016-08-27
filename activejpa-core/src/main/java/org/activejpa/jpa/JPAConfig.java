/**
 * 
 */
package org.activejpa.jpa;

import javax.persistence.EntityManagerFactory;

/**
 * @author ganeshs
 *
 */
public class JPAConfig {

	private final String name;
	
	private final EntityManagerProvider entityManagerProvider;
	
	private ThreadLocal<JPAContext> currentContext = new ThreadLocal<JPAContext>();
	
	public JPAConfig(String name, EntityManagerProvider provider) {
		this.name = name;
		this.entityManagerProvider = provider;
	}

	public void close() {
		try {
			getEntityManagerFactory().close();
		} catch (Exception e) {
			// Suppress exception and log
		}
	}
	
	public JPAContext getContext() {
		return getContext(false);
	}
	
	public JPAContext getContext(boolean readOnly) {
		JPAContext context = currentContext.get();
		if (context == null) {
			context = new JPAContext(this, readOnly);
			currentContext.set(context);
		}
		return context;
	}
	
	protected void clearContext() {
		currentContext.remove();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the entityManagerFactory
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerProvider.getEntityManagerFactory();
	}
}
