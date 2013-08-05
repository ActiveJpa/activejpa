/**
 * 
 */
package org.activejpa.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;

/**
 * @author ganeshs
 *
 */
public class JPA {
	
	public static final JPA instance = new JPA();
	
	private JPAConfig defaultConfig;
	
	private Map<String, JPAConfig> configs = new HashMap<String, JPAConfig>();
	
	private String cacheableHint;
	
	private static final String HIBERNATE_PERSISTENCE = "org.hibernate.ejb.HibernatePersistence";
	
	private static final String ECLIPSE_PERSISTENCE = "org.eclipse.persistence.jpa.PersistenceProvider";
	
	private static final String OPENJPA_PERSISTENCE = "org.apache.openjpa.persistence.PersistenceProviderImpl";
	
	private JPA() {
		List<PersistenceProvider> providers = PersistenceProviderResolverHolder.getPersistenceProviderResolver().getPersistenceProviders();
		if (providers != null) {
			String providerClass = providers.get(0).getClass().getCanonicalName();
			if (providerClass.equals(HIBERNATE_PERSISTENCE)) {
				cacheableHint = "org.hibernate.cacheable";
			} else if (providerClass.equals(ECLIPSE_PERSISTENCE)) {
				cacheableHint = "eclipselink.query-results-cache";
			} else if (providerClass.equals(OPENJPA_PERSISTENCE)) {
				cacheableHint = "openjpa.QueryCache";
			}
		}
	}
	
	public void addPersistenceUnit(String persistenceUnitName) {
		addPersistenceUnit(persistenceUnitName, true);
	}
	
	public void addPersistenceUnit(String persistenceUnitName, boolean isDefault) {
		addPersistenceUnit(persistenceUnitName, Collections.<String, String>emptyMap(), isDefault);
	}

	public void addPersistenceUnit(String persistenceUnitName, Map<String, String> properties, boolean isDefault) {
		EntityManagerFactory factory = createEntityManagerFactory(persistenceUnitName, properties);
		addPersistenceUnit(persistenceUnitName, factory, isDefault);
	}
	
	public void addPersistenceUnit(String persistenceUnitName, EntityManagerFactory factory) {
		addPersistenceUnit(persistenceUnitName, factory, true);
	}
	
	public void addPersistenceUnit(String persistenceUnitName, EntityManagerFactory factory, boolean isDefault) {
		JPAConfig config = new JPAConfig(persistenceUnitName, factory);
		if (isDefault) {
			defaultConfig = config;
		}
		configs.put(persistenceUnitName, config);
	}
	
	public JPAConfig getConfig(String configName) {
		return configs.get(configName);
	}
	
	/**
	 * @return the defaultConfig
	 */
	public JPAConfig getDefaultConfig() {
		return defaultConfig;
	}

	public void close() {
		List<JPAConfig> confs = new ArrayList<JPAConfig>();
		confs.addAll(configs.values());
		for (JPAConfig config : confs) {
			config.close();
			configs.remove(config.getName());
		}
	}
	
	protected EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map<String, String> properties) {
		return Persistence.createEntityManagerFactory(persistenceUnitName, properties);	
	}

	/**
	 * @return the cacheableHint
	 */
	public String getCacheableHint() {
		return cacheableHint;
	}
}
