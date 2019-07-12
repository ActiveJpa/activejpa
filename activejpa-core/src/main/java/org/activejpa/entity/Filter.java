/**
 * 
 */
package org.activejpa.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.activejpa.entity.Condition.Operator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ganeshs
 *
 */
@Getter(AccessLevel.PACKAGE)
public class Filter {
	
	private List<Condition> conditions = new ArrayList<Condition>();
	
	private List<SortField> sortFields = new ArrayList<SortField>();

	private int pageNo;
	
	private int perPage;
	
	private boolean cacheable;
	
	@Getter(AccessLevel.NONE)
	private boolean shouldPage;
	
	@Setter(AccessLevel.NONE)
	private Class entityClass;
	
	Filter(Class<?> clazz) {
		this();
		this.entityClass = clazz;
	}
	
	public Filter(int perPage, int pageNo, Condition... conditions) {
		page(pageNo, perPage);
		if (conditions != null) {
			this.conditions.addAll(Arrays.asList(conditions));
		}
	}
	
	public Filter() {
		this(0, 0);
	}
	
	public Filter(Condition... conditions) {
		this(0, 0, conditions);
	}
	
	public Filter condition(String name, Object value) {
		return condition(name, Operator.eq, value);
	}
	
	public Filter condition(String name, Operator operator, Object value) {
		this.conditions.add(new Condition(name, operator, value));
		return this;
	}
	
	public Filter page(int pageNo, int perPage) {
		this.pageNo = pageNo > 0 ? pageNo : 1;
		this.perPage = (perPage < 1) ? Integer.MAX_VALUE : perPage;
		return this;
	}
	
	public Filter sortBy(String name, boolean asc) {
		return sortBy(new SortField(name, asc));
	}
	
	public Filter sortBy(SortField sortField) {
		this.sortFields.add(sortField);
		return this;
	}
	
	public Filter cacheable(boolean cacheable) {
		this.cacheable = cacheable;
		return this;
	}
	
	/**
	 * Constructs the select criteria query
	 * 
	 * @param builder
	 * @param query
	 * @param root
	 */
	<T extends Model> void constructQuery(CriteriaBuilder builder, CriteriaQuery<?> query, Root<T> root) {
		if (!conditions.isEmpty()) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Condition condition : conditions) {
				predicates.add(condition.constructQuery(builder, root));
			}
			query.where(predicates.toArray(new Predicate[0]));
		}
		
		if (!sortFields.isEmpty()) {
			List<Order> orders = new ArrayList<Order>();
			for (SortField sortField : sortFields) {
				orders.add(sortField.getOrder(builder, root));
			}
			query.orderBy(orders);
		}
	}
	
	/**
	 * Constructs the delete criteria query
	 * 
	 * @param builder
	 * @param query
	 * @param root
	 */
	<T extends Model> void constructQuery(CriteriaBuilder builder, CriteriaDelete<?> query, Root<T> root) {
		if (!conditions.isEmpty()) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Condition condition : conditions) {
				predicates.add(condition.constructQuery(builder, root));
			}
			query.where(predicates.toArray(new Predicate[0]));
		}
	}
	
	Filter setParameters(Query query) {
		if (!conditions.isEmpty()) {
			for (Condition condition : conditions) {
				condition.setParameters(query, condition.getValue());
			}
		}
		return this;
	}
	
	Filter setPage(Query query) {
		query.setFirstResult((getPageNo() - 1) * getPerPage());
		query.setMaxResults(getPerPage());
		return this;
	}
	
	public Filter clone(boolean paginate) {
		Filter filter = new Filter();
		filter.conditions = conditions;
		filter.sortFields = sortFields;
		if (paginate) {
			filter.page(pageNo, perPage);
			filter.shouldPage = shouldPage;
		}
		return filter;
	}
	
	public <T> List<T> list() {
		if (entityClass == null) {
			throw new IllegalStateException("Entity class not set");
		}
		return Model.where(entityClass, this);
	}
	
	public long count() {
		if (entityClass == null) {
			throw new IllegalStateException("Entity class not set");
		}
		return Model.count(entityClass, this);
	}
	
	public void delete() {
		if (entityClass == null) {
			throw new IllegalStateException("Entity class not set");
		}
		Model.deleteAll(entityClass, this);
	}
}
