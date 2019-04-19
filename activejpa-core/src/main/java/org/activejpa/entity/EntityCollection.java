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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.activejpa.ActiveJpaException;
import org.activejpa.util.PropertyUtil;
import org.javalite.common.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

/**
 * @author ganeshs
 *
 */
@RequiredArgsConstructor
public class EntityCollection<T extends Model> extends BaseObject {
	
	private final Model parent;
	
	private final String name;
	
	private final Class<T> elementType;
	
	private static final Logger logger = LoggerFactory.getLogger(EntityCollection.class);
	
	/**
	 * Adds the item to the underlying collection
	 */
	public T add(T item) {
		logger.debug("Adding the item {} to the collection", item);
		return addOrRemove(item, true);
	}
	
	/**
	 * Removes the item from the underlying collection
	 */
	public T remove(T item) {
		logger.debug("Removing the item {} from the collection", item);
		return addOrRemove(item, false);
	}
	
	/**
	 * Adds or Removes the item to/from the underlying collection. This method attempts in the following order to add/remove the item,
	 * <ul>
	 * <li> Checks if there's a method with the signature <code>addOrderItem(OrderItem orderItem)</code> in the parent model where orderItem is a collection.
	 * <li> Retrieves the getter for the collection property and adds/removes the item to/from that
	 * <li> Retrieves the collection property field and adds/removes the item to/from that
	 * </ul>
	 * 
	 * Throws {@link ActiveJpaException} if none of the above ways adds/removes the item to/from the collection
	 * @param item
	 */
	@SuppressWarnings("unchecked")
	protected T addOrRemove(T item, boolean add) {
		Method method = null;
		String methodName = getMethodName(add ? "add" : "remove", name);
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
		logger.trace("Attempting to invoke the getter for the property {} on the parent {}", name, parent);
		Collection<T> collection = (Collection<T>) PropertyUtil.getProperty(parent, name);
		if (collection != null) {
			if (add) {
				collection.add(item);
			} else {
				collection.remove(item);
			}
			return item;
		}
		
		// Try to find out the field and add/remove it to/from that
		try {
			logger.trace("Attempting to invoke the the property {} on the parent {}", name, parent);
			Field field = parent.getClass().getDeclaredField(name);
			field.setAccessible(true);
			collection = (Collection<T>) field.get(parent);
			if (add) {
				collection.add(item);
			} else {
				collection.remove(item);
			}
			return item;
		} catch (Exception e) {
			logger.error("Failed to {} the item {} to the collection of the parent", add ? "add" : "remove", item, parent);
			throw new ActiveJpaException("Failed while adding/removing the item to the collection - " + name, e);
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
		return createCollectionQuery(filter).getSingleResult();
	}
	
	public List<T> where(Filter filter) {
		return createCollectionQuery(filter).getResultList();
	}
	
	public List<T> where(Object... paramValues) {
		return where(createFilter(paramValues));
	}
	
	public long count(Filter filter) {
		filter.condition("id", parent.getId());
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cQuery = builder.createQuery(Long.class);
		Root<? extends Model> root = cQuery.from(parent.getClass());
		Join join = root.join(name);
		cQuery.select(builder.count(join));
		filter.constructQuery(builder, cQuery, root);
		
		TypedQuery<Long> query = createQuery(cQuery, filter);
		return query.getSingleResult();
	}
	
	private TypedQuery<T> createCollectionQuery(Filter filter) {
		filter.condition("id", parent.getId());
		TypedQuery<T> query = createQuery(parent.getClass(), name, elementType, filter);
		return query;
	}
	
	public void persist() {
		parent.persist();
	}
	
	/**
	 * Constructs the name of the method from the collectionName 
	 * 
	 * @param prefix
	 * @param collectionName
	 * @return
	 */
	private String getMethodName(String prefix, String collectionName) {
		collectionName = Inflector.singularize(collectionName);
		return prefix + collectionName.substring(0, 1).toUpperCase() + collectionName.substring(1);
	}
}
