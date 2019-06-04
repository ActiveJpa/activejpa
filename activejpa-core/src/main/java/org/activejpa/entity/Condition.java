/**
 * 
 */
package org.activejpa.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.activejpa.util.ConvertUtil;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ganeshs
 *
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class Condition extends AbstractConstruct {

	/**
	 * @author ganeshs
	 *
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public enum Operator {
		eq {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.equal(path, parameter[0]);
			} 
		},
		ne {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.notEqual(path, parameter[0]);
			}
		},
		le {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.lessThanOrEqualTo(path, parameter[0]);
			}
		},
		lt {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.lessThan(path, parameter[0]);
			}
		},
		ge {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.greaterThanOrEqualTo(path, parameter[0]);
			}
		}, 
		gt {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.greaterThan(path, parameter[0]);
			}
		},
		between {
			@Override
			Predicate constructCondition(CriteriaBuilder builder, Path path, String name) {
				name = cleanName(name);
				return createPredicate(builder, path, builder.parameter(path.getJavaType(), "from" + name), builder.parameter(path.getJavaType(), "to" + name));
			}
			
			@Override
			void setParameters(Query query, String name, Object value, Class<?> paramType) {
				name = cleanName(name);
				Object[] values = null;
				if (value instanceof Object[]) {
					values = (Object[]) value;
				}
				if (values == null || values.length != 2) {
					throw new IllegalArgumentException("Value - " + value + " should be an array of size 2");
				}
				super.setParameters(query, "from" + name, values[0], paramType);
				super.setParameters(query, "to" + name, values[1], paramType);
			}
			
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.between(path, parameter[0], parameter[1]);
			}
		}, 
		in {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return path.in(parameter[0]);
			}
			
			@Override
			void setParameters(Query query, String name, Object value, Class<?> paramType) {
				name = cleanName(name);
				if (value instanceof Object[]) {
					value = Arrays.asList((Object[]) value);
				}
				List list = new ArrayList();
				for (Object val : (List) value) {
					list.add(ConvertUtil.convert(val, paramType));
				}
				query.setParameter(name, list);
			}
			
			@Override
			Predicate constructCondition(CriteriaBuilder builder, Path path, String name) {
				name = cleanName(name);
				return createPredicate(builder, path, builder.parameter(Collection.class, name));
			}
		},
		not_in {
			@Override
			Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return path.in(parameter[0]).not();
			}
			
			@Override
			void setParameters(Query query, String name, Object value, Class<?> paramType) {
				name = cleanName(name);
				if (value instanceof Object[]) {
					value = Arrays.asList((Object[]) value);
				}
				List list = new ArrayList();
				for (Object val : (List) value) {
					list.add(ConvertUtil.convert(val, paramType));
				}
				query.setParameter(name, list);
			}
			
			@Override
			Predicate constructCondition(CriteriaBuilder builder, Path path, String name) {
				name = cleanName(name);
				return createPredicate(builder, path, builder.parameter(Collection.class, name));
			}
		},
		like {
			@Override
			protected Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.like(path, parameter[0]);
			}
		},
		not_like {
			@Override
			protected Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.notLike(path, parameter[0]);
			}
		},
		is_null {
			@Override
			protected Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.isNull(path);
			}
			@Override
			void setParameters(Query query, String name, Object value, Class<?> paramType) {
			}
		},
		is_not_null {
			@Override
			protected Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter) {
				return builder.isNotNull(path);
			}
			@Override
			void setParameters(Query query, String name, Object value, Class<?> paramType) {
			}
		};
		
		void setParameters(Query query, String name, Object value, Class<?> paramType) {
			name = cleanName(name);
			value = ConvertUtil.convert(value, paramType);
			query.setParameter(name, value);
		}
		
		Predicate constructCondition(CriteriaBuilder builder, Path path, String name) {
			return createPredicate(builder, path, builder.parameter(path.getJavaType(), cleanName(name)));
		}
		
		abstract Predicate createPredicate(CriteriaBuilder builder, Path path, Expression... parameter);
		
		/**
		 * Cleans the name to align with the jpa query language constructs
		 *  
		 * @param name
		 * @return
		 */
		String cleanName(String name) {
			return name.replace(".", "0_");
		}
	}
	
	private String name;
	
	private Object value;
	
	private Operator operator;
	
	@Setter(AccessLevel.PACKAGE)
	@Getter(AccessLevel.NONE)
	private Path<?> path;
	
	public Condition(String name, Operator operator) {
		this(name, operator, null);
		if (operator != Operator.is_null && operator != Operator.is_not_null) {
			throw new IllegalArgumentException("Right hand side value missing for the operator " + operator);
		}
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
	
	protected <T extends Model> Predicate constructQuery(CriteriaBuilder builder, Root<T> root) {
		path = getPath(root, name);
		return operator.constructCondition(builder, path, name);
	}
	
	protected void setParameters(Query query, Object value) {
		operator.setParameters(query, name, value, path.getJavaType());
	}
}
