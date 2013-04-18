/**
 * 
 */
package org.activejpa.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author ganeshs
 *
 */
public class DatabaseMetadata {
	
	private DatabaseMetaData metaData;
	
	private Map<String, TableMetadata> tables = new HashMap<String, TableMetadata>();
	
	public DatabaseMetadata(Connection connection) throws SQLException {
		this.metaData = connection.getMetaData();
	}
	
	public TableMetadata getTableMetadata(String catalog, String schema, String tableName) throws SQLException {
		String tableId = getTableId(catalog, schema, tableName);
		TableMetadata table = tables.get(tableId);
		if (table != null) {
			return table;
		}
		ResultSet rs = null;
		try {
			rs = metaData.getTables(catalog, schema, tableName, new String[] {"TABLE", "VIEW"});
			while (rs.next()) {
				String name = rs.getString("TABLE_NAME");
				if (name.equalsIgnoreCase(tableName)) {
					table = new TableMetadata(metaData, rs);
					tables.put(tableId, table);
					return table;
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return table;
	}
	
	private String getTableId(String catalog, String schema, String tableName) {
		StringBuffer buffer = new StringBuffer();
		if (catalog != null && catalog.length() > 0) {
			buffer.append(catalog).append(".");
		}
		if (schema != null && schema.length() > 0) {
			buffer.append(schema).append(".");
		}
		return buffer.append(tableName).toString();
	}

	/**
	 * @return the metaData
	 */
	public DatabaseMetaData getMetaData() {
		return metaData;
	}

	/**
	 * @return the tables
	 */
	public Map<String, TableMetadata> getTables() {
		return tables;
	}

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/dbconfig.properties"));
		DBConfig config = new DBConfig(properties);
		DriverManager.registerDriver((Driver)Class.forName(config.getDriverClass()).newInstance());
		Connection connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
		DatabaseMetadata metadata = new DatabaseMetadata(connection);
		TableMetadata tableMetadata = metadata.getTableMetadata(null, null, "schema_version");
		for (ColumnMetadata columnMetadata : tableMetadata.getColumns().values()) {
			System.out.println(columnMetadata.getName() + " " + columnMetadata.getTypeName() + " " + columnMetadata.getTypeCode());
		}
		System.out.println(tableMetadata);
	}
}
