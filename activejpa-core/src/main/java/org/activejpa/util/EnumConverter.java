/**
 * 
 */
package org.activejpa.util;

import org.apache.commons.beanutils.Converter;

/**
 * @author ganeshs
 *
 */
public class EnumConverter implements Converter {
	
	public static final EnumConverter instance = new EnumConverter();
	
	private EnumConverter() {
	}

	public Object convert(Class type, Object value) {
		if (Enum.class.isAssignableFrom(type)) {
			return Enum.valueOf(type, value.toString());
		}
		return null;
	}
}