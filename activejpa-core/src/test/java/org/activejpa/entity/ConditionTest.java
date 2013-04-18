/**
 * 
 */
package org.activejpa.entity;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import javax.persistence.Query;

import org.activejpa.entity.Condition.Operator;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ConditionTest {

	@Test
	public void shouldUseEqWhenOperatorNotSpecified() {
		Condition condition = new Condition("key", "value");
		assertEquals(condition.getOperator(), Operator.eq);
	}
	
	@Test
	public void shouldConstructEqQuery() {
		Condition condition = new Condition("key", Operator.eq, "value");
		assertEquals(condition.constructQuery(), "key = ? ");
	}
	
	@Test
	public void shouldSetParametersForEqQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.eq, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructNeQuery() {
		Condition condition = new Condition("key", Operator.ne, "value");
		assertEquals(condition.constructQuery(), "key != ? ");
	}
	
	@Test
	public void shouldSetParametersForNeQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.ne, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructLtQuery() {
		Condition condition = new Condition("key", Operator.lt, "value");
		assertEquals(condition.constructQuery(), "key < ? ");
	}
	
	@Test
	public void shouldSetParametersForLtQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.lt, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructGtQuery() {
		Condition condition = new Condition("key", Operator.gt, "value");
		assertEquals(condition.constructQuery(), "key > ? ");
	}
	
	@Test
	public void shouldSetParametersForGtQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.gt, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructLeQuery() {
		Condition condition = new Condition("key", Operator.le, "value");
		assertEquals(condition.constructQuery(), "key <= ? ");
	}
	
	@Test
	public void shouldSetParametersForLeQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.le, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructGeQuery() {
		Condition condition = new Condition("key", Operator.ge, "value");
		assertEquals(condition.constructQuery(), "key >= ? ");
	}
	
	@Test
	public void shouldSetParametersForGeQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.ge, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructInQuery() {
		Condition condition = new Condition("key", Operator.in, Arrays.asList("value"));
		assertEquals(condition.constructQuery(), "key in ? ");
	}
	
	@Test
	public void shouldSetParametersForInQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.in, Arrays.asList("value"));
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructLikeQuery() {
		Condition condition = new Condition("key", Operator.like, "value");
		assertEquals(condition.constructQuery(), "key like ? ");
	}
	
	@Test
	public void shouldSetParametersForLikeQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.like, "value");
		condition.setParameters(query, 1, "value");
		verify(query).setParameter(1, "value");
	}
	
	@Test
	public void shouldConstructBetweenQuery() {
		Condition condition = new Condition("key", Operator.between, new Object[]{"value1", "value2"});
		assertEquals(condition.constructQuery(), "key between ? and ? ");
	}
	
	@Test
	public void shouldSetParametersForBetweenQuery() {
		Query query = mock(Query.class);
		Condition condition = new Condition("key", Operator.between, new Object[]{"value1", "value2"});
		condition.setParameters(query, 1, new String[]{"value1", "value2"});
		verify(query).setParameter(1, "value1");
		verify(query).setParameter(2, "value2");
	}
}
