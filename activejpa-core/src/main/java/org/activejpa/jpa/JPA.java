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

/**
 * @author ganeshs
 *
 */
public class JPA {
	
	public static final JPA instance = new JPA();
	
	private JPAConfig defaultConfig;
	
	private Map<String, JPAConfig> configs = new HashMap<String, JPAConfig>();
	
	private JPA() {
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
}
