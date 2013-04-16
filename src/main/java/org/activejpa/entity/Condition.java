/**
 * 
 */
package org.activejpa.entity;

import javax.persistence.Query;

/**
 * @author ganeshs
 *
 */
public class Condition {

	/**
	 * @author ganeshs
	 *
	 */
	public enum Operator {
		eq("="), 
		ne("!="), 
		le("<="), 
		lt("<"), 
		ge(">="), 
		gt(">"), 
		between("between") {
			@Override
			public String constructCondition(String name, Object value) {
				return name + " between ? and ? ";
			}
			
			@Override
			public int setParameters(Query query, String name, Object value, int position) {
				Object[] values = null;
				if (value instanceof Object[]) {
					values = (Object[]) value;
				}
				if (values == null || values.length != 2) {
					throw new IllegalArgumentException("Value - " + value + " should be an array of size 2");
				}
				query.setParameter(position++, values[0]);
				query.setParameter(position++, values[1]);
				return position;
			}
		}, 
		in("in"), 
		like("like");
		
		private String operator;
		
		private Operator(String operator) {
			this.operator = operator;
		}
		
		public String constructCondition(String name, Object value) {
			return name + " " + operator + " ? ";
		}
		
		public int setParameters(Query query, String name, Object value, int position) {
			query.setParameter(position++, value);
			return position;
		}
	}
	
	private String name;
	
	private Object value;
	
	private Operator operator;
	
	/**
	 * Default constructor
	 */
	public Condition() {
	}
	
	/**
	 * @param name
	 * @param operator
	 * @param value
	 */
	public Condition(String name, Operator operator, Object value) {
		this.name = name;
		this.value = value;
		this.operator = operator;
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public Condition(String name, Object value) {
		this(name, Operator.eq, value);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String constructQuery() {
		return operator.constructCondition(name, value);
	}
	
	public int setParameters(Query query, int position, Object value) {
		return operator.setParameters(query, name, value, position);
	}
}
