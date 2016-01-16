/**
 * 
 */
package org.activejpa.entity;

import org.activejpa.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

/**
 * @author ganeshs
 *
 */
class BaseObject {
	
	protected static <T> TypedQuery<T> createQuery(CriteriaQuery<T> cQuery, Filter filter) {
		TypedQuery<T> query = getEntityManager().createQuery(cQuery);
		updateQueryParams(query, filter);
		return query;
	}
	
	protected static <T> Query createQuery(CriteriaDelete<T> cQuery, Filter filter) {
		Query query = getEntityManager().createQuery(cQuery);
		updateQueryParams(query, filter);
		return query;
	}
	
	private static void updateQueryParams(Query query, Filter filter) {
		filter.setParameters(query);
		filter.setPage(query);
		query.setHint(JPA.instance.getCacheableHint(), filter.isCacheable());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static <T extends Model, S extends Model> TypedQuery<S> createQuery(Class<T> entityType, String attribute, Class<S> attributeType, Filter filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<S> cQuery = builder.createQuery(attributeType);
		//
		cQuery.distinct(filter.isDistinct());
		//
		Root<T> root = cQuery.from(entityType);
		if (attribute != null) {
			Join join = root.join(attribute);
			cQuery.select(join);
		}
		filter.constructQuery(builder, cQuery, root);
		return createQuery(cQuery, filter);
	}
	
	protected static <T extends Model> TypedQuery<T> createQuery(Class<T> clazz, Filter filter) {
		return createQuery(clazz, null, clazz, filter);
	}
	
	protected static <T extends Model> TypedQuery<T> createQuery(Class<T> clazz, Object... paramValues) {
		return createQuery(clazz, createFilter(paramValues));
	}
	
	protected static Filter createFilter(Object... paramValues) {
		Filter filter = new Filter();
		if (paramValues != null) {
			for (int i = 0; i < paramValues.length; i += 2) {
				filter.addCondition(paramValues[i].toString(), paramValues[i + 1]);
			}
		}
		return filter;
	}
	
	protected static EntityManager getEntityManager() {
		return JPA.instance.getDefaultConfig().getContext().getEntityManager();
	}
}
