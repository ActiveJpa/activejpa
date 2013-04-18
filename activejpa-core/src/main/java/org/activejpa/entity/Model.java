/**
 * 
 */
package org.activejpa.entity;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;



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
	
	public static EntityTransaction beginTxn() {
		JPA.instance.getDefaultConfig().getContext().beginTxn();
		return getEntityManager().getTransaction();
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
		return JPA.instance.getDefaultConfig().getContext().getEntityManager();
	}
	
	protected static <T extends Model> long count(final Class<T> clazz) {
		return getEntityManager().createQuery("select count(*) from " + clazz.getSimpleName(), Long.class).getSingleResult();
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
	
	public void persist() {
		execute(new Executor<Void>() {
			@Override
			public Void execute(EntityManager manager) {
				manager.persist(Model.this);
				return null;
			}
		}, false);
	}
	
	public void delete() {
		execute(new Executor<Void>() {
			@Override
			public Void execute(EntityManager manager) {
				manager.remove(Model.this);
				return null;
			}
		}, false);
	}
	
	public void merge() {
		execute(new Executor<Void>() {
			@Override
			public Void execute(EntityManager manager) {
				manager.merge(Model.this);
				return null;
			}
		}, false);
	}
	
	public void refresh() {
		execute(new Executor<Void>() {
			@Override
			public Void execute(EntityManager manager) {
				manager.refresh(Model.this);
				return null;
			}
		}, true);
	}
	
	protected static <T> T execute(Executor<T> executor, boolean readOnly) {
		JPAContext context = JPA.instance.getDefaultConfig().getContext();
		boolean beganTxn = false;
		if (! context.isTxnOpen()) {
			context.beginTxn();
			beganTxn = true;
		}
		try {
			return executor.execute(getEntityManager());
		} finally {
			if (beganTxn) {
				context.closeTxn(readOnly);
			}
		}
	}
	
	private static interface Executor<T> {
		
		T execute(EntityManager manager);
	}
}