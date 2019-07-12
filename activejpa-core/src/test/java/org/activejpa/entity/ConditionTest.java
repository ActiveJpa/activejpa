/**
 * 
 */
package org.activejpa.entity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.activejpa.entity.Condition.Operator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ConditionTest {
	
	private Path path;
	
	private ParameterExpression expression;
	
	private CriteriaBuilder builder;
	
	private Root<? extends Model> root;
	
	private Query query;
	
	@BeforeMethod
	public void setup() {
		builder = mock(CriteriaBuilder.class);
		path = mock(Path.class);
		when(path.getJavaType()).thenReturn(String.class);
		expression = mock(ParameterExpression.class);
		root = mock(Root.class);
		query = mock(Query.class);
		Parameter param = mock(Parameter.class);
		when(param.getParameterType()).thenReturn(String.class);
		when(query.getParameter(anyString())).thenReturn(param);
		
	}

	@Test
	public void shouldUseEqWhenOperatorNotSpecified() {
		Condition condition = new Condition("key", "value");
		assertEquals(condition.getOperator(), Operator.eq);
	}
	
	@Test
	public void shouldConstructEqCriteriaQuery() {
		Condition condition = new Condition("key", Operator.eq, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).equal(path, expression);
	}
	
	@Test
	public void shouldSetParametersForEqQuery() {
		Condition condition = new Condition("key", Operator.eq, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructNeCriteriaQuery() {
		Condition condition = new Condition("key", Operator.ne, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).notEqual(path, expression);
	}
	
	@Test
	public void shouldSetParametersForNeQuery() {
		Condition condition = new Condition("key", Operator.ne, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructLtCriteriaQuery() {
		Condition condition = new Condition("key", Operator.lt, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).lessThan(path, expression);
	}
	
	@Test
	public void shouldSetParametersForLtQuery() {
		Condition condition = new Condition("key", Operator.lt, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructGtCriteriaQuery() {
		Condition condition = new Condition("key", Operator.gt, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).greaterThan(path, expression);
	}
	
	@Test
	public void shouldSetParametersForGtQuery() {
		Condition condition = new Condition("key", Operator.gt, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructLeCriteriaQuery() {
		Condition condition = new Condition("key", Operator.le, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).lessThanOrEqualTo(path, expression);
	}
	
	@Test
	public void shouldSetParametersForLeQuery() {
		Condition condition = new Condition("key", Operator.le, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructGeCriteriaQuery() {
		Condition condition = new Condition("key", Operator.ge, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).greaterThanOrEqualTo(path, expression);
	}
	
	@Test
	public void shouldSetParametersForGeQuery() {
		Condition condition = new Condition("key", Operator.ge, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructInCriteriaQuery() {
		Condition condition = new Condition("key", Operator.in, Arrays.asList("value"));
		when(builder.parameter(Collection.class, "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(path).in(expression);
	}
	
	@Test
	public void shouldSetParametersForInQuery() {
		Object value = Arrays.asList("value");
		Condition condition = new Condition("key", Operator.in, value);
		condition.setPath(path);
		condition.setParameters(query, value);
		verify(query).setParameter("key", value);
	}
	
	@Test
	public void shouldSetParametersForInQueryUsingArray() {
		Object[] value = new Object[] {"value"};
		Condition condition = new Condition("key", Operator.in, value);
		condition.setPath(path);
		condition.setParameters(query, value);
		verify(query).setParameter("key", Arrays.asList(value));
	}
	
	@Test
	public void shouldConstructNotInCriteriaQuery() {
		Condition condition = new Condition("key", Operator.not_in, Arrays.asList("value"));
		when(builder.parameter(Collection.class, "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		Predicate predicate = mock(Predicate.class);
		when(path.in(expression)).thenReturn(predicate);
		condition.constructQuery(builder, root);
		verify(path).in(expression);
		verify(predicate).not();
	}
	
	@Test
	public void shouldSetParametersForNotInQuery() {
		Object value = Arrays.asList("value");
		Condition condition = new Condition("key", Operator.not_in, value);
		condition.setPath(path);
		condition.setParameters(query, value);
		verify(query).setParameter("key", value);
	}
	
	@Test
	public void shouldSetParametersForNotInQueryUsingArray() {
		Object[] value = new Object[] {"value"};
		Condition condition = new Condition("key", Operator.not_in, value);
		condition.setPath(path);
		condition.setParameters(query, value);
		verify(query).setParameter("key", Arrays.asList(value));
	}
	
	@Test
	public void shouldConstructLikeCriteriaQuery() {
		Condition condition = new Condition("key", Operator.like, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).like(path, expression);
	}
	
	@Test
	public void shouldConstructNotLikeCriteriaQuery() {
		Condition condition = new Condition("key", Operator.not_like, "value");
		when(builder.parameter(path.getJavaType(), "key")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).notLike(path, expression);
	}
	
	@Test
	public void shouldSetParametersForLikeQuery() {
		Condition condition = new Condition("key", Operator.like, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldSetParametersForNotLikeQuery() {
		Condition condition = new Condition("key", Operator.not_like, "value");
		condition.setPath(path);
		condition.setParameters(query, "value");
		verify(query).setParameter("key", "value");
	}
	
	@Test
	public void shouldConstructBetweenCriteriaQuery() {
		Condition condition = new Condition("key", Operator.between, new String[]{"value", "value2"});
		when(builder.parameter(path.getJavaType(), "fromkey")).thenReturn(expression);
		when(builder.parameter(path.getJavaType(), "tokey")).thenReturn(expression);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).between(path, expression, expression);
	}
	
	@Test
	public void shouldSetParametersForBetweenQuery() {
		Condition condition = new Condition("key", Operator.between, new Object[]{"value1", "value2"});
		condition.setPath(path);
		condition.setParameters(query, new String[]{"value1", "value2"});
		verify(query).setParameter("fromkey", "value1");
		verify(query).setParameter("tokey", "value2");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value -.*should be an array of size 2")
	public void shouldThrowExceptionOnSetParametersForBetweenQueryWithInvalidValueType() {
		Condition condition = new Condition("key", Operator.between, new Object[]{"value1", "value2"});
		condition.setPath(path);
		condition.setParameters(query, "value1");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value -.*should be an array of size 2")
	public void shouldThrowExceptionOnSetParametersForBetweenQueryWithInvalidLength() {
		Condition condition = new Condition("key", Operator.between, new Object[]{"value1", "value2"});
		condition.setPath(path);
		condition.setParameters(query, new Object[]{"value1"});
	}
	
	@Test
	public void shouldConstructIsNullQuery() {
		Condition condition = new Condition("key", Operator.is_null);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).isNull(path);
	}
	
	@Test
	public void shouldConstructIsNotNullQuery() {
		Condition condition = new Condition("key", Operator.is_not_null);
		when(root.get("key")).thenReturn(path);
		condition.constructQuery(builder, root);
		verify(builder).isNotNull(path);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfRHSMissingForNonNullOperators() {
		new Condition("key", Operator.eq);
	}
}
