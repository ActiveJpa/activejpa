/**
 * 
 */
package org.activejpa.db;

import java.util.Properties;

/**
 * @author ganeshs
 *
 */
public class DBConfig {
	
	private static final String DRIVER_CLASS = "driver_class";
	
	private static final String JDBC_URL = "jdbc_url";
	
	private static final String USERNAME = "username";
	
	private static final String PASSWORD = "password";
	
	private String driverClass;
	
	private String url;
	
	private String username;
	
	private String password;
	
	/**
	 * @param driverClass
	 * @param url
	 * @param username
	 * @param password
	 */
	public DBConfig(String driverClass, String url, String username,
			String password) {
		this.driverClass = driverClass;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public DBConfig(Properties properties) {
		this((String)properties.get(DRIVER_CLASS), (String)properties.get(JDBC_URL), (String)properties.get(USERNAME), (String)properties.get(PASSWORD));
	}

	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
}
