/**
 * 
 */
package org.activejpa.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;

/**
 * @author ganeshs
 *
 */
public class EntityCollection<T extends Model> extends BaseObject {
	
	private Model parent;
	
	private String name;
	
	private Class<T> elementType;
	
	/**
	 * @param parent
	 * @param name
	 */
	public EntityCollection(Model parent, String name, Class<T> elementType) {
		this.parent = parent;
		this.name = name;
		this.elementType = elementType;
	}

	public T findById(Serializable id) {
		return one(name + ".id", id);
	}
	
	public T first(Object... paramValues) {
		List<T> list = where(createFilter(paramValues));
		if (list == null) {
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
}
