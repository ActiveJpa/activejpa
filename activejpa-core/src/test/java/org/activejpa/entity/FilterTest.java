/**
 * 
 */
package org.activejpa.entity;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
		Filter filter = new Filter()
				.addCondition("testKey", Operator.eq, "testValue")
				.addCondition("testKey1", Operator.eq, "testValue1");
		assertEquals(filter.constructQuery(), "testKey = :testKey and testKey1 = :testKey1");
	}
	
	@Test
	public void shouldConstructCriteriaQuery() {
		Filter filter = new Filter(mock(Condition.class), mock(Condition.class));
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		Root root = mock(Root.class);
		Predicate p1 = mock(Predicate.class);
		Predicate p2 = mock(Predicate.class);
		when(filter.getConditions().get(0).constructQuery(builder, root)).thenReturn(p1);
		when(filter.getConditions().get(1).constructQuery(builder, root)).thenReturn(p2);
		CriteriaQuery query = mock(CriteriaQuery.class);
		filter.constructQuery(builder, query, root);
		verify(query).where(p1, p2);
	}
	
	@Test
	public void shouldConstructCriteriaDeleteQuery() {
		Filter filter = new Filter(mock(Condition.class), mock(Condition.class));
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		Root root = mock(Root.class);
		Predicate p1 = mock(Predicate.class);
		Predicate p2 = mock(Predicate.class);
		when(filter.getConditions().get(0).constructQuery(builder, root)).thenReturn(p1);
		when(filter.getConditions().get(1).constructQuery(builder, root)).thenReturn(p2);
		CriteriaDelete query = mock(CriteriaDelete.class);
		filter.constructQuery(builder, query, root);
		verify(query).where(p1, p2);
	}
	
	@Test
	public void shouldConstructCriteriaQueryWithSortFields() {
		Filter filter = new Filter().addSortField(mock(SortField.class)).addSortField(mock(SortField.class));
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		Root root = mock(Root.class);
		Order order1 = mock(Order.class);
		Order order2 = mock(Order.class);
		when(filter.getSortFields().get(0).getOrder(builder, root)).thenReturn(order1);
		when(filter.getSortFields().get(1).getOrder(builder, root)).thenReturn(order2);
		CriteriaQuery query = mock(CriteriaQuery.class);
		filter.constructQuery(builder, query, root);
		verify(query).orderBy(Arrays.asList(order1, order2));
	}
	
	@Test
	public void shouldSetParameters() {
		Filter filter = new Filter()
				.addCondition("testKey", Operator.eq, "testValue")
				.addCondition("testKey1", Operator.eq, "testValue1");
		Path path = mock(Path.class);
		when(path.getJavaType()).thenReturn(String.class);
		filter.getConditions().get(0).setPath(path);
		filter.getConditions().get(1).setPath(path);
		Query query = mock(Query.class);
		Parameter param = mock(Parameter.class);
		when(param.getParameterType()).thenReturn(String.class);
		when(query.getParameter(anyString())).thenReturn(param);
		filter.setParameters(query);
		verify(query).setParameter("testKey", "testValue");
		verify(query).setParameter("testKey1", "testValue1");
	}
	
	@Test
	public void shouldCloneFilter() {
		Filter filter = new Filter();
		Filter clonedFilter = filter.clone(false);
		assertTrue(clonedFilter.getConditions().isEmpty());
		assertEquals(clonedFilter.getPageNo(), Integer.valueOf(1));
		assertEquals(clonedFilter.getPerPage(), Integer.valueOf(Integer.MAX_VALUE));
	}
	
	@Test
	public void shouldCloneFilterWithConditions() {
		Filter filter = new Filter(mock(Condition.class));
		Filter clonedFilter = filter.clone(false);
		assertEquals(clonedFilter.getConditions().size(), 1);
	}
	
	@Test
	public void shouldCloneFilterWithConditionsWithPaginate() {
		Filter filter = new Filter(2, 20, mock(Condition.class));
		Filter clonedFilter = filter.clone(true);
		assertEquals(clonedFilter.getConditions().size(), 1);
		assertEquals(clonedFilter.getPageNo(), Integer.valueOf(20));
		assertEquals(clonedFilter.getPerPage(), Integer.valueOf(2));
	}
	
	@Test
	public void shouldCloneFilterWithConditionsWithoutPaginate() {
		Filter filter = new Filter(2, 20, mock(Condition.class));
		Filter clonedFilter = filter.clone(false);
		assertEquals(clonedFilter.getConditions().size(), 1);
		assertEquals(clonedFilter.getPageNo(), Integer.valueOf(1));
		assertEquals(clonedFilter.getPerPage(), Integer.valueOf(Integer.MAX_VALUE));
	}
	
	@Test
	public void shouldNotPageIfPerPageIsNotSet() {
		Filter filter = new Filter(mock(Condition.class));
		assertFalse(filter.shouldPage());
	}
	
	@Test
	public void shouldPageIfPerPageIsSet() {
		Filter filter = new Filter(10, 1, mock(Condition.class));
		assertTrue(filter.shouldPage());
	}
	
	@Test
	public void shouldOrderByPrimaryTableField() {
		
	}
}
