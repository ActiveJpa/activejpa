/**
 * 
 */
package org.activejpa.util;

import java.lang.reflect.Method;

import org.activejpa.ActiveJpaException;

/**
 * @author ganeshs
 *
 */
public class ConvertUtil {

	/**
	 * @param value
	 * @param targetType
	 */
	public static Object convert(Object value, Class<?> targetType) {
		try {
			Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("org.apache.commons.beanutils.ConvertUtils");
			Method method = clazz.getMethod("convert", Object.class, Class.class);
			return method.invoke(null, value, targetType);
		} catch (Exception e) {
			throw new ActiveJpaException("Failed while converting the type", e);
		}
	}
}
