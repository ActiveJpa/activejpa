/**
 * 
 */
package org.activejpa.entity;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

import javax.persistence.Query;

import org.activejpa.entity.Condition.Operator;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class FilterTest {

	@Test
	public void shouldSetDefaultValuesWhenInitialized() {
		Filter filter = new Filter();
		assertEquals(filter.getPageNo(), (Integer)1);
		assertEquals(filter.getPerPage(), (Integer)Integer.MAX_VALUE);
	}
	
	@Test
	public void shouldSetActualValuesWhenInitialized() {
		Filter filter = new Filter(10, 2);
		assertEquals(filter.getPageNo(), (Integer)2);
		assertEquals(filter.getPerPage(), (Integer)10);
	}
	
	@Test
	public void shouldAddConditionsInConstructor() {
		Filter filter = new Filter(mock(Condition.class), mock(Condition.class));
		assertEquals(filter.getConditions().size(), 2);
	}
	
	@Test
	public void shouldAddConditionUsingNameValue() {
		Filter filter = new Filter();
		filter.addCondition("testKey", "testValue");
		assertEquals(filter.getConditions().size(), 1);
		assertEquals(filter.getConditions().get(0), new Condition("testKey", "testValue"));
	}
	
	@Test
	public void shouldAddConditionUsingNameValueOperator() {
		Filter filter = new Filter();
		filter.addCondition("testKey", Operator.eq, "testValue");
		assertEquals(filter.getConditions().size(), 1);
		assertEquals(filter.getConditions().get(0), new Condition("testKey", Operator.eq, "testValue"));
	}
	
	@Test
	public void shouldConstructQuery() {
		Filter filter = new Filter();
		filter.addCondition("testKey", Operator.eq, "testValue");
		filter.addCondition("testKey1", Operator.eq, "testValue1");
		assertEquals(filter.constructQuery(), "testKey = :testKey and testKey1 = :testKey1");
	}
	
	@Test
	public void shouldSetParameters() {
		Filter filter = new Filter();
		filter.addCondition("testKey", Operator.eq, "testValue");
		filter.addCondition("testKey1", Operator.eq, "testValue1");
		Query query = mock(Query.class);
		filter.setParameters(query);
		verify(query).setParameter("testKey", "testValue");
		verify(query).setParameter("testKey1", "testValue1");
	}
}
