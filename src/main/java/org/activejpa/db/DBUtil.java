/**
 * 
 */
package org.activejpa.db;


/**
 * @author ganeshs
 *
 */
public class DBUtil {

	public static String getTableName(String className) {
		StringBuffer tableName = new StringBuffer();
		for (int i = 0; i < className.length(); i++) {
			char character = className.charAt(i);
			if (Character.isLowerCase(character)) {
				tableName.append(character);
			} else {
				if (i != 0) {
					tableName.append("_");
				}
				tableName.append(Character.toLowerCase(character));
			}
		}
		return tableName.toString();
	}
	
	public static String getFieldName(String columnName) {
		StringBuffer fieldName = new StringBuffer();
		boolean makeUppercase = false;
		for (int i = 0; i < columnName.length(); i++) {
			char character = columnName.charAt(i);
			if (character == '_') {
				makeUppercase = true;
			} else { 
				character = makeUppercase ? Character.toUpperCase(character) : Character.toLowerCase(character);
				fieldName.append(character);
				makeUppercase = false;
			}
		}
		return fieldName.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(DBUtil.getTableName("NewModelColumn"));
		System.out.println(DBUtil.getFieldName("new_model").toCharArray()[7]);
	}
}
