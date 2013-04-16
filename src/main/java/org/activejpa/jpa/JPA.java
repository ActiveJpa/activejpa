/**
 * 
 */
package org.activejpa.jpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author ganeshs
 *
 */
public final class JPA {
	
	private static JPAConfig defaultConfig;
	
	private static Map<String, JPAConfig> configs = new HashMap<String, JPAConfig>();
	
	public static void addPersistenceUnit(String persistenceUnitName) {
		addPersistenceUnit(persistenceUnitName, true);
	}
	
	public static void addPersistenceUnit(String persistenceUnitName, boolean isDefault) {
		addPersistenceUnit(persistenceUnitName, Collections.<String, String>emptyMap(), isDefault);
	}

	public static void addPersistenceUnit(String persistenceUnitName, Map<String, String> properties, boolean isDefault) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnitName);
		addPersistenceUnit(persistenceUnitName, factory, isDefault);
	}
	
	public static void addPersistenceUnit(String persistenceUnitName, EntityManagerFactory factory) {
		addPersistenceUnit(persistenceUnitName, factory, true);
	}
	
	public static void addPersistenceUnit(String persistenceUnitName, EntityManagerFactory factory, boolean isDefault) {
		JPAConfig config = new JPAConfig(persistenceUnitName, factory);
		if (isDefault) {
			defaultConfig = config;
		}
		configs.put(persistenceUnitName, config);
	}
	
	public static JPAConfig getConfig(String configName) {
		return configs.get(configName);
	}
	
	/**
	 * @return the defaultConfig
	 */
	public static JPAConfig getDefaultConfig() {
		return defaultConfig;
	}

	public static void close() {
		for (JPAConfig config : configs.values()) {
			config.close();
		}
	}
}
