/**
 * 
 */
package org.activejpa.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.activejpa.ActiveJpaException;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class EntityCollection<T extends Model> extends BaseObject {
	
	private Model parent;
	
	private String name;
	
	private Class<T> elementType;
	
	private static final Logger logger = LoggerFactory.getLogger(EntityCollection.class);
	
	/**
	 * @param parent
	 * @param name
	 */
	public EntityCollection(Model parent, String name, Class<T> elementType) {
		this.parent = parent;
		this.name = name;
		this.elementType = elementType;
	}
	
	/**
	 * Adds the item to the underlying collection. This method attempts in the following order to add the item,
	 * <ul>
	 * <li> Checks if there's a method with the signature <code>addOrderItem(OrderItem orderItem)</code> in the parent model where orderItem is a collection.
	 * <li> Retrieves the getter for the collection property and adds the item to that
	 * <li> Retrieves the collection property field and adds the item to that
	 * </ul>
	 * 
	 * Throws {@link ActiveJpaException} if none of the above ways adds the item to the collection
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	public T add(T item) {
		logger.debug("Adding the item {} to the collection", item);
		Method method = null;
		String methodName = getMethodName("add", name);
		try {
			
			logger.trace("Attempting to invoke the method {} on the parent {}", methodName, parent);
			method = parent.getClass().getDeclaredMethod(methodName, elementType);
			method.invoke(parent, item);
			return item;
		} catch (NoSuchMethodException e) {
			logger.debug("Method {} doesn't exist in the class {}", methodName, parent.getClass());
		} catch (Exception e) {
			throw new ActiveJpaException("Failed while invoking the method " + method.getName(), e);
		}
		
		// Try adding the item to the collection property returned by the getter
		try {
			logger.trace("Attempting to invoke the getter for the property {} on the parent {}", name, parent);
			Collection<T> collection = (Collection<T>)PropertyUtils.getProperty(parent, name); 
			collection.add(item);
			return item;
		} catch (NoSuchMethodException e) {
			logger.debug("Getter doesn't exist for the property {} in the class {}", name, parent.getClass());
		} catch (Exception e) {
			throw new ActiveJpaException("Failed while invoking the getter for the property " + name + " in the class " + parent.getClass(), e);
		}
		
		// Try to find out the field and add it to that
		try {
			logger.trace("Attempting to invoke the the property {} on the parent {}", name, parent);
			Field field = parent.getClass().getDeclaredField(name);
			field.setAccessible(true);
			Collection<T> collection = (Collection<T>) field.get(parent);
			collection.add(item);
			return item;
		} catch (Exception e) {
			logger.error("Failed to add the item {} to the collection of the parent", item, parent);
			throw new ActiveJpaException("Failed while adding the item to the collection - " + name, e);
		}
	}

	public T findById(Serializable id) {
		return one(name + ".id", id);
	}
	
	public List<T> all() {
		return where(new Filter());
	}
	
	public T first(Object... paramValues) {
		List<T> list = where(createFilter(paramValues));
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	public T one(Object... paramValues) {
		Filter filter = createFilter(paramValues);
		TypedQuery<T> query = createCollectionQuery(filter);
		filter.setParameters(query);
		return query.getSingleResult();
	}
	
	public List<T> where(Filter filter) {
		return createCollectionQuery(filter).getResultList();
	}
	
	public List<T> where(Object... paramValues) {
		return where(createFilter(paramValues));
	}
	
	private TypedQuery<T> createCollectionQuery(Filter filter) {
		filter.addCondition("id", parent.getId());
		TypedQuery<T> query = createQuery(parent.getClass(), name, elementType, filter);
		filter.setParameters(query);
		return query;
	}
	
	public void persist() {
		parent.persist();
	}
	
	private String getMethodName(String prefix, String collectionName) {
		if (collectionName.endsWith("s")) {
			collectionName = collectionName.substring(0, collectionName.length() - 1);
		}
		return prefix + collectionName.substring(0, 1).toUpperCase() + collectionName.substring(1);
	}
}
