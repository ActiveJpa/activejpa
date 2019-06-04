/**
 * 
 */
package org.activejpa.entity;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

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
import org.activejpa.entity.testng.ActiveJpaAgentObjectFactory;
import org.activejpa.jpa.JPA;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FilterTest {
	
	/**
	 * HACK. `mvn test` will be run before the package is created. javaagent can be loaded only from a jar. Since the
	 * jar is not yet created, it will throw agent not found exception. This is a hack to get rid of that exception
	 */
	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
		return new ActiveJpaAgentObjectFactory();
	}
	
	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}

	@Test
	public void shouldSetDefaultValuesWhenInitialized() {
		Filter filter = new Filter();
		assertEquals(filter.getPageNo(), 1);
		assertEquals(filter.getPerPage(), Integer.MAX_VALUE);
	}
	
	@Test
	public void shouldSetActualValuesWhenInitialized() {
		Filter filter = new Filter(10, 2);
		assertEquals(filter.getPageNo(), 2);
		assertEquals(filter.getPerPage(), 10);
	}
	
	@Test
	public void shouldSetPage() {
		Filter filter = new Filter();
		filter.page(2, 20);
		assertEquals(filter.getPageNo(), 2);
		assertEquals(filter.getPerPage(), 20);
	}
	
	@Test
	public void shouldFixPageIfOutOfBounds() {
		Filter filter = new Filter();
		filter.page(0, 0);
		assertEquals(filter.getPageNo(), 1);
		assertEquals(filter.getPerPage(), Integer.MAX_VALUE);
	}
	
	@Test
	public void shouldAddConditionsInConstructor() {
		Filter filter = new Filter(mock(Condition.class), mock(Condition.class));
		assertEquals(filter.getConditions().size(), 2);
	}
	
	@Test
	public void shouldNotAddNullConditionsInConstructor() {
		Filter filter = new Filter(0, 0, (Condition[]) null);
		assertTrue(filter.getConditions().isEmpty());
	}
	
	@Test
	public void shouldAddConditionUsingNameValue() {
		Filter filter = new Filter();
		filter.condition("testKey", "testValue");
		assertEquals(filter.getConditions().size(), 1);
		assertEquals(filter.getConditions().get(0).getName(), "testKey");
		assertEquals(filter.getConditions().get(0).getValue(), "testValue");
	}
	
	@Test
	public void shouldAddConditionUsingNameValueOperator() {
		Filter filter = new Filter();
		filter.condition("testKey", Operator.eq, "testValue");
		assertEquals(filter.getConditions().size(), 1);
		assertEquals(filter.getConditions().get(0).getName(), "testKey");
		assertEquals(filter.getConditions().get(0).getOperator(), Operator.eq);
		assertEquals(filter.getConditions().get(0).getValue(), "testValue");
	}
	
	@Test
	public void shouldAddSortFieldsByNameAndOrder() {
		Filter filter = new Filter();
		filter.sortBy("column1", false);
		assertEquals(filter.getSortFields().size(), 1);
		assertEquals(filter.getSortFields().get(0).getName(), "column1");
		assertEquals(filter.getSortFields().get(0).isAsc(), false);
	}
	
	@Test
	public void shouldAddSortFieldsByObject() {
		Filter filter = new Filter();
		filter.sortBy(new SortField("column1", false));
		assertEquals(filter.getSortFields().size(), 1);
		assertEquals(filter.getSortFields().get(0).getName(), "column1");
		assertEquals(filter.getSortFields().get(0).isAsc(), false);
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
		Filter filter = new Filter().sortBy(mock(SortField.class)).sortBy(mock(SortField.class));
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
				.condition("testKey", Operator.eq, "testValue")
				.condition("testKey1", Operator.eq, "testValue1");
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
	public void shouldNotSetParametersIfConditionsIsEmpty() {
		Filter filter = new Filter();
		Path path = mock(Path.class);
		when(path.getJavaType()).thenReturn(String.class);
		Query query = mock(Query.class);
		filter.setParameters(query);
		verify(query, never()).setParameter(anyString(), anyString());
	}
	
	@Test
	public void shouldSetPageInQuery() {
		Filter filter = new Filter(6, 1);
		Query query = mock(Query.class);
		filter.setPage(query);
		verify(query).setFirstResult(0);
		verify(query).setMaxResults(6);
	}
	
	@Test
	public void shouldCloneFilter() {
		Filter filter = new Filter();
		Filter clonedFilter = filter.clone(false);
		assertTrue(clonedFilter.getConditions().isEmpty());
		assertEquals(clonedFilter.getPageNo(), 1);
		assertEquals(clonedFilter.getPerPage(), Integer.MAX_VALUE);
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
		assertEquals(clonedFilter.getPageNo(), 20);
		assertEquals(clonedFilter.getPerPage(), 2);
	}
	
	@Test
	public void shouldCloneFilterWithConditionsWithoutPaginate() {
		Filter filter = new Filter(2, 20, mock(Condition.class));
		Filter clonedFilter = filter.clone(false);
		assertEquals(clonedFilter.getConditions().size(), 1);
		assertEquals(clonedFilter.getPageNo(), 1);
		assertEquals(clonedFilter.getPerPage(), Integer.MAX_VALUE);
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldThrowExceptionOnGetResultListIfEntityClassIsNotSet() {
		Filter filter = new Filter();
		filter.condition("column1", "column1");
		filter.getResultList();
	}
	
	@Test
	public void shouldGetResultList() {
		createModel("column1", "column2");
		Filter filter = new Filter(DummyModel.class);
		filter.condition("column1", "column1");
		List<DummyModel> models = filter.getResultList();
		assertEquals(models.size(), 1);
	}
	
	@Test
	public void shouldSetCacheable() {
		Filter filter = new Filter(2, 20);
		filter.cacheable(true);
		assertTrue(filter.isCacheable());
		filter.cacheable(false);
		assertFalse(filter.isCacheable());
	}
	
	private DummyModel createModel(String column1, String column2) {
		DummyModel model = new DummyModel();
		model.setColumn1(column1);
		model.setColumn2(column2);
		model.persist();
		return model;
	}
}
