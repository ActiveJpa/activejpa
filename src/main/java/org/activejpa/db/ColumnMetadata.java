/**
 * 
 */
package org.activejpa.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * @author ganeshs
 *
 */
public class ColumnMetadata {
	
	public enum Type {
		VARCHAR, DOUBLE, FLOAT, INTEGER, BIGINT, DATETIME, TIMESTAMP, BOOL, TINYINT 
	}
	
	private final String name;
	
	private final String typeName;
	
	private final int columnSize;
	
	private final int decimalDigits;
	
	private final String isNullable;
	
	private final int typeCode;
	
	private final boolean primaryKey;
	
	private final boolean autoIncrement;
	
	private final boolean generated;
	
	public ColumnMetadata(ResultSet rs, boolean primaryKey) throws SQLException {
		name = rs.getString("COLUMN_NAME");
		columnSize = rs.getInt("COLUMN_SIZE");
		decimalDigits = rs.getInt("DECIMAL_DIGITS");
		isNullable = rs.getString("IS_NULLABLE");
		typeCode = rs.getInt("DATA_TYPE");
		typeName = new StringTokenizer( rs.getString("TYPE_NAME"), "() ").nextToken();
		this.primaryKey = primaryKey;
		autoIncrement = rs.getBoolean("IS_AUTOINCREMENT");
		generated = false;//TODO rs.getBoolean("IS_GENERATEDCOLUMN");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @return the columnSize
	 */
	public int getColumnSize() {
		return columnSize;
	}

	/**
	 * @return the decimalDigits
	 */
	public int getDecimalDigits() {
		return decimalDigits;
	}

	/**
	 * @return the isNullable
	 */
	public String getIsNullable() {
		return isNullable;
	}

	/**
	 * @return the typeCode
	 */
	public int getTypeCode() {
		return typeCode;
	}

	/**
	 * @return the primaryKey
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @return the autoIncrement
	 */
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	/**
	 * @return the generated
	 */
	public boolean isGenerated() {
		return generated;
	}

}
