/**
 * 
 */
package org.activejpa.util;

import static org.activejpa.util.PropertyUtil.getCollectionElementType;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class BeanUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);

	/**
	 * <p>Utility method that will be used to load bean from a map output of json de-serialization</p>
	 *  
	 * @param model
	 * @param attributes
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public static void load(Object model, Map<String, Object> attributes) {
		for (Entry<String, Object> entry : attributes.entrySet()) {
			try {
				Method method = PropertyUtil.getReadMethod(model, entry.getKey());
				if (method == null) {
					continue;
				}
				Type propertyType = method.getGenericReturnType() != null ? method.getGenericReturnType() : method.getReturnType();
				Object property = PropertyUtil.getProperty(model, entry.getKey());
				if (PropertyUtil.isSimpleProperty(entry.getValue().getClass())) {
					PropertyUtil.setProperty(model, entry.getKey(), entry.getValue());
				} else if (PropertyUtil.isCollectionProperty(propertyType, false)) {
					// TODO Need to figure out a way to populate collection
				} else if (PropertyUtil.isMapProperty(propertyType)) {
					if (property == null) {
						property = new HashMap();
					}
					loadMap((Map)property, (Map)attributes.get(entry.getKey()), getCollectionElementType(propertyType));
					PropertyUtil.setProperty(model, entry.getKey(), property);
				} else {
					if (property == null && propertyType instanceof Class) {
						property = ((Class)propertyType).newInstance();
					}
					load(property, (Map<String, Object>) entry.getValue());
					PropertyUtil.setProperty(model, entry.getKey(), property);
				}
			} catch (Exception e) {
				logger.warn("Failed while loading the attributes to the bean", e);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	private static void loadMap(Map map, Map attributes, Class<?> elementType) throws Exception {
		for (Entry entry : (Set<Entry>) attributes.entrySet()) {
			Object value = map.get(entry.getKey());
			if (value == null) {
				value = elementType.newInstance();
			}
			load(value, (Map)entry.getValue());
			map.put(entry.getKey(), value);
		}
	}
}
