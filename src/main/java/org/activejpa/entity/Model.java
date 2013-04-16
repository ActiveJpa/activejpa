/**
 * 
 */
package org.activejpa.entity;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.activejpa.jpa.JPA;



/**
 * <p> Base class for all entities. Embeds entity manager in it and provides a bunch of DAL abstractions to make data access a lot simpler.
 * The static methods are to be implemented by the entity classes and will be done at runtime through activejpa instrumentation. </p>
 * 
 * @author ganeshs
 *
 */
public abstract class Model {
	
	private static final String NIE = "Your models are not instrumented. Make sure you run with -javaagent:activejpa-instrument.jar";
	
	public abstract Serializable getId();
	
	public static <T extends Model> T findById(Serializable id) {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static long count() {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static <T extends Model> List<T> all() {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static void deleteAll() {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static boolean exists(Serializable id) {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static <T extends Model> List<T> where(Object... paramValues) {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static <T extends Model> List<T> where(Filter filter) {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static <T extends Model> T one(Object... paramValues) {
		throw new UnsupportedOperationException(NIE);
	}
	
	public static <T extends Model> T first(Object... paramValues) {
		throw new UnsupportedOperationException(NIE);
	}
	
	protected static <T extends Model> T findById(Class<T> clazz, Serializable id) {
		return getEntityManager().find(clazz, id);
	}
	
	protected static <T extends Model> T one(Class<T> clazz, Object... paramValues) {
		return createQuery(clazz, paramValues).getSingleResult();
	}
	
	protected static <T extends Model> T first(Class<T> clazz, Object... paramValues) {
		List<T> list = where(clazz, paramValues);
		if (list != null && ! list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	protected static <T extends Model> List<T> where(Class<T> clazz, Object... paramValues) {
		return createQuery(clazz, paramValues).getResultList();
	}
	
	protected static <T extends Model> List<T> where(Class<T> clazz, Filter filter) {
		return createQuery(clazz, filter).getResultList();
	}
	
	private static <T extends Model> TypedQuery<T> createQuery(Class<T> clazz, Filter filter) {
		StringWriter writer = new StringWriter().append("from ").append(clazz.getSimpleName());
		if (! filter.getConditions().isEmpty()) {
			writer.append(" where ").append(filter.constructQuery());
		}
		TypedQuery<T> query = getEntityManager().createQuery(writer.toString(), clazz);
		filter.setParameters(query, 1);
		return query;
	}
	
	private static <T extends Model> TypedQuery<T> createQuery(Class<T> clazz, Object... paramValues) {
		Filter filter = new Filter();
		if (paramValues != null) {
			for (int i = 0; i < paramValues.length; i += 2) {
				filter.addCondition(paramValues[i].toString(), paramValues[i + 1]);
			}
		}
		return createQuery(clazz, filter);
	}
	
	protected static EntityManager getEntityManager() {
		return JPA.getDefaultConfig().getContext().getEntityManager();
	}
	
	protected static <T extends Model> long count(Class<T> clazz) {
		return getEntityManager().createQuery("select count(1) from " + clazz.getSimpleName()).getFirstResult();
	}
	
	protected static <T extends Model> List<T> all(Class<T> clazz) {
		return getEntityManager().createQuery("from " + clazz.getSimpleName(), clazz).getResultList();
	}
	
	protected static <T extends Model> void deleteAll(Class<T> clazz) {
		getEntityManager().createQuery("delete from " + clazz.getSimpleName()).executeUpdate();
	}
	
	protected static <T extends Model> boolean exists(Class<T> clazz, Serializable id) {
		return findById(clazz, id) != null;
	}
	
	public boolean exists() {
		return exists(getId());
	}
	
	public void persist() {
		getEntityManager().persist(this);
	}
	
	public void delete() {
		getEntityManager().remove(this);
	}
	
	public void merge() {
		getEntityManager().merge(this);
	}
	
	public void refresh() {
		getEntityManager().refresh(this);
	}
}