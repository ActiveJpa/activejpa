/**
 * 
 */
package org.activejpa.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ganeshs
 *
 */
public class TableMetadata {
	
	private final String catalog;
	
	private final String schema;
	
	private final String name;
	
	private final Map<String, ColumnMetadata> columns = new HashMap<String, ColumnMetadata>();
	
	private final List<String> primaryKeys = new ArrayList<String>();

	public TableMetadata(DatabaseMetaData metaData, ResultSet rs) throws SQLException {
		catalog = rs.getString("TABLE_CAT");
		schema = rs.getString("TABLE_SCHEM");
		name = rs.getString("TABLE_NAME");
		initPrimaryKeys(metaData);
		initColumns(metaData);
	}
	
	protected void initPrimaryKeys(DatabaseMetaData metaData) throws SQLException {
		ResultSet rs = metaData.getPrimaryKeys(catalog, schema, name);
		while (rs.next()) {
			primaryKeys.add(rs.getString("COLUMN_NAME"));
		}
	}
	
	protected void initColumns(DatabaseMetaData metaData) throws SQLException {
		ResultSet rs = metaData.getColumns(this.catalog, this.schema, this.name, "%");
		if (rs != null) {
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME");
				if (! columns.containsKey(name)) {
					columns.put(name, new ColumnMetadata(rs, primaryKeys.contains(name)));
				}
			}
		}
	}

	/**
	 * @return the catalog
	 */
	public String getCatalog() {
		return catalog;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the columns
	 */
	public Map<String, ColumnMetadata> getColumns() {
		return columns;
	}
}
