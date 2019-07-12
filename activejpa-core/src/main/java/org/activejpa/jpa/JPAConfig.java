/**
 * 
 */
package org.activejpa.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class JPAConfig {

	private final String name;
	
	private final EntityManagerProvider entityManagerProvider;
	
	private ThreadLocal<JPAContext> currentContext = new ThreadLocal<JPAContext>();
	
	private static final Logger logger = LoggerFactory.getLogger(JPAConfig.class);
	
	public JPAConfig(String name, EntityManagerProvider provider) {
		this.name = name;
		this.entityManagerProvider = provider;
	}

	public void close() {
		try {
			if (entityManagerProvider.getEntityManagerFactory().isOpen()) {
				entityManagerProvider.getEntityManagerFactory().close();
			} else {
				logger.info("Entity manager factory is not open");
			}
		} catch (Exception e) {
			logger.warn("Failed while closing the entity manager factory", e);
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

	public EntityManagerProvider getEntityManagerProvider() {
		return entityManagerProvider;
	}

}
