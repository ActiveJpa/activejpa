/**
 * 
 */
package org.activejpa.entity;

import java.io.StringWriter;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ganeshs
 *
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper=false)
public class Filter {
	
	private List<Condition> conditions = new ArrayList<Condition>();
	
	private List<SortField> sortFields = new ArrayList<SortField>();

	private Integer pageNo;
	
	private Integer perPage;
	
	private boolean cacheable;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean shouldPage;
	
	@Setter(AccessLevel.NONE)
	private Class entityClass;
	
	Filter(Class<?> clazz) {
		this.entityClass = clazz;
	}
	
	public Filter(int perPage, int pageNo, Condition... conditions) {
		this.pageNo = pageNo > 0 ? pageNo : 1;
		this.shouldPage = perPage > 0;
		this.perPage = (perPage < 1) ? Integer.MAX_VALUE : perPage;
		if (conditions != null && conditions.length > 0) {
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
	
	public Filter sortBy(String name, boolean asc) {
		return sortBy(new SortField(name, asc));
	}
	
	public Filter sortBy(SortField sortField) {
		this.sortFields.add(sortField);
		return this;
	}
	
	public Filter cacheable(boolean cacheable) {
		setCacheable(cacheable);
		return this;
	}
	
	@Deprecated
	public Filter addCondition(String name, Object value) {
		return condition(name, value);
	}
	
	@Deprecated
	public Filter addCondition(String name, Operator operator, Object value) {
		return condition(name, operator, value);
	}
	
	@Deprecated
	public Filter addSortField(String name, boolean asc) {
		return sortBy(name, asc);
	}
	
	@Deprecated
	public Filter addSortField(SortField sortField) {
		return sortBy(sortField);
	}
	
	String constructQuery() {
		StringWriter writer = new StringWriter();
		if (conditions != null || !conditions.isEmpty()) {
			for (int i = 0; i < conditions.size() - 1; i++) {
				writer.append(conditions.get(i).constructQuery()).append(" and ");
			}
			writer.append(conditions.get(conditions.size() - 1).constructQuery());
		}
		return writer.toString();
	}
	
	/**
	 * Constructs the select criteria query
	 * 
	 * @param builder
	 * @param query
	 * @param root
	 */
	<T extends Model> void constructQuery(CriteriaBuilder builder, CriteriaQuery<?> query, Root<T> root) {
		if (conditions != null || !conditions.isEmpty()) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Condition condition : conditions) {
				predicates.add(condition.constructQuery(builder, root));
			}
			query.where(predicates.toArray(new Predicate[0]));
		}
		
		if (sortFields != null && !sortFields.isEmpty()) {
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
	protected <T extends Model> void constructQuery(CriteriaBuilder builder, CriteriaDelete<?> query, Root<T> root) {
		if (conditions != null || !conditions.isEmpty()) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Condition condition : conditions) {
				predicates.add(condition.constructQuery(builder, root));
			}
			query.where(predicates.toArray(new Predicate[0]));
		}
	}
	
	protected Filter setParameters(Query query) {
		if (conditions != null && !conditions.isEmpty()) {
			for (Condition condition : conditions) {
				condition.setParameters(query, condition.getValue());
			}
		}
		return this;
	}
	
	Filter setPage(Query query) {
		if (shouldPage) {
			query.setFirstResult((getPageNo() - 1) * getPerPage());
			query.setMaxResults(getPerPage());
		}
		return this;
	}
	
	public Filter clone(boolean paginate) {
		Filter filter = new Filter(conditions.toArray(new Condition[0]));
		if (paginate) {
			filter.perPage = perPage;
			filter.pageNo = pageNo;
			filter.shouldPage = shouldPage;
		}
		return filter;
	}
	
	public <T> List<T> getResultList() {
		if (entityClass == null) {
			throw new IllegalStateException("Entity class not set");
		}
		return Model.createQuery(entityClass, this).getResultList();
	}
}
