/**
 * 
 */
package org.activejpa.entity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.activejpa.entity.Condition.Operator;

/**
 * @author ganeshs
 *
 */
public class Filter {
	
	private List<Condition> conditions = new ArrayList<Condition>();

	private Integer pageNo;
	
	private Integer perPage;
	
	public Filter(int perPage, int pageNo, Condition... conditions) {
		this.pageNo = pageNo > 0 ? pageNo : 1;
		this.perPage = (perPage < 1) ? Integer.MAX_VALUE : perPage;
		if (conditions != null && conditions.length > 0) {
			this.conditions = Arrays.asList(conditions);
		}
	}
	
	public Filter() {
		this(0, 0);
	}
	
	public Filter(Condition... conditions) {
		this(0, 0, conditions);
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPerPage() {
		return perPage;
	}

	public void setPerPage(Integer perPage) {
		this.perPage = perPage;
	}
	
	public Integer getStart() {
		return (pageNo - 1) * perPage;
	}
	
	public Integer getLimit() {
		return perPage;
	}

	/**
	 * @return the conditions
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public void addCondition(String name, Object value) {
		addCondition(name, Operator.eq, value);
	}
	
	public void addCondition(String name, Operator operator, Object value) {
		this.conditions.add(new Condition(name, operator, value));
	}
	
	public String constructQuery() {
		StringWriter writer = new StringWriter();
		if (conditions != null || !conditions.isEmpty()) {
			for (int i = 0; i < conditions.size() - 1; i++) {
				writer.append(conditions.get(i).constructQuery()).append(" and ");
			}
			writer.append(conditions.get(conditions.size() - 1).constructQuery());
		}
		return writer.toString();
	}
	
	public int setParameters(Query query, int startPosition) {
		if (conditions != null && !conditions.isEmpty()) {
			for (Condition condition : conditions) {
				startPosition = condition.setParameters(query, startPosition, condition.getValue());
			}
		}
		return startPosition;
	}
}
