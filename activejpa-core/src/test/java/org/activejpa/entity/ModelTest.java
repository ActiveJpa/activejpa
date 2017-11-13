/**
 * 
 */
package org.activejpa.entity;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.activejpa.entity.Condition;
import org.activejpa.entity.EntityCollection;
import org.activejpa.entity.Filter;
import org.activejpa.entity.testng.BaseModelTest;
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
public class ModelTest extends BaseModelTest {
	
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
	public void shouldCreateModel() {
		createModel("column1", "column2");
		assertEquals(DummyModel.count(), 1);
	}
	
	@Test
	public void shouldFindById() {
		DummyModel model = createModel("column1", "column2");;
		assertEquals(DummyModel.findById(model.getId()), model);
	}
	
	@Test
	public void shouldFindAll() {
		DummyModel model = createModel("column1", "column2");
		DummyModel model1 = createModel("column1", "column2");
		assertEquals(DummyModel.all(), Arrays.asList(model, model1));
	}
	
	@Test
	public void shouldDeleteAll() {
		createModel("column1", "column2");
		createModel("column1", "column2");
		DummyModel.deleteAll();
		assertEquals(DummyModel.count(), 0);
	}
	
	@Test
	public void shouldDeleteAllByFilter() {
		createModel("test234", "test");
		createModel("test123", "test");
		DummyModel.deleteAll(new Filter(new Condition("column1", "test123")));
		assertEquals(DummyModel.count(), 1);
	}
	
	@Test
	public void shouldSearchByKeyValue() {
		DummyModel model = createModel("test123", "test");
		createModel("test1234", "test");
		assertEquals(DummyModel.where(new Object[]{"column1", "test123"}), Arrays.asList(model));
	}
	
	@Test
	public void shouldSearchByMultipleKeyValues() {
		DummyModel model = createModel("test123", "test125");
		createModel("test1234", "test125");
		assertEquals(DummyModel.where(new Object[]{"column1", "test123", "column2", "test125"}), Arrays.asList(model));
	}
	
	@Test
	public void shouldSearchUsingFilter() {
		DummyModel model = createModel("test123", "test125");
		createModel("test1234", "test125");
		assertEquals(DummyModel.where(new Filter(new Condition("column1", "test123"), new Condition("column2", "test125"))), Arrays.asList(model));
	}
	
	@Test
	public void shouldCheckExists() {
		DummyModel model = new DummyModel();
		model.persist();
		assertTrue(DummyModel.exists(model.getId()));
	}
	
	@Test
	public void shouldSearchAndReturnFirst() {
		DummyModel model = createModel("test123", "test125");
		createModel("test123", "test125");
		assertEquals(DummyModel.first(new Object[]{"column1", "test123", "column2", "test125"}), model);
	}
	
	@Test
	public void shouldSearchAndReturnTheOnlyMatch() {
		DummyModel model = createModel("test123", "test125");
		createModel("test124", "test125");
		assertEquals(DummyModel.one(new Object[]{"column1", "test123", "column2", "test125"}), model);
	}
	
	@Test(expectedExceptions=NonUniqueResultException.class)
	public void shouldThrowExceptionIfMoreThanOneMatches() {
		createModel("test123", "test125");
		createModel("test123", "test125");
		DummyModel.one(new Object[]{"column1", "test123", "column2", "test125"});
	}
	
	@Test(expectedExceptions=NoResultException.class)
	public void shouldThrowExceptionIfNoMatcheFound() {
		DummyModel.one(new Object[]{"column1", "test123", "column2", "test125"});
	}
	
	@Test
	public void shouldBeginTransaction() {
		EntityTransaction txn = JPA.instance.getDefaultConfig().getContext().getEntityManager().getTransaction();
		txn.commit();
		assertFalse(txn.isActive());
		DummyModel.beginTxn();
		assertTrue(txn.isActive());
	}
	
	@Test
	public void shouldMergeWithEntity() {
		DummyModel model = createModel("test", "test123");
		DummyModel model1 = new DummyModel();
		model1.setId(model.getId());
		model1.setColumn1("test123");
		model1.setColumn2("test");
		model1.merge();
		model = DummyModel.findById(model.getId());
		assertEquals(model1.getColumn1(), model.getColumn1());
		assertEquals(model1.getColumn2(), model.getColumn2());
	}
	
	@Test
	public void shouldDeleteEntity() {
		DummyModel model = createModel("test", "test123");
		model.delete();
		assertNull(DummyModel.findById(model.getId()));
	}
	
	@Test
	public void shouldCountByFilter() {
		createModel("test", "test123");
		createModel("test1", "test124");
		createModel("test1", "test124");
		assertEquals(DummyModel.count(new Filter(new Condition("column2", "test124"))), 2);
	}
	
	@Test
	public void shouldSearchByFilterJoin() {
		DummyModel parent = createModel("test", "test123");
		DummyModel child1 = createModel("test1", "test124");
		DummyModel child2 = createModel("test1", "test124");
		parent.addChild(child1);
		parent.addChild(child2);
		parent.persist();
		List<DummyModel> list = DummyModel.where(new Filter(new Condition("children.column1", "test1"), new Condition("column1", "test")));
		assertEquals(list.size(), 2);
	}
	
	@Test
	public void shouldGetCollection() {
		DummyModel parent = createModel("test", "test123");
		DummyModel child1 = createModel("test1", "test124");
		DummyModel child2 = createModel("test2", "test123");
		parent.addChild(child1);
		parent.addChild(child2);
		parent.persist();
		EntityCollection<DummyModel> collection = parent.collection("children");
		assertNotNull(collection);
	}
	
	@Test
	public void shouldUpdateAttributes() {
		DummyModel parent = createModel("test", "test123");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("column1", "test1");
		map.put("column2", "test2");
		parent.updateAttributes(map);
		assertEquals(parent.getColumn1(), "test1");
		assertEquals(parent.getColumn2(), "test2");
	}
	
	private DummyModel createModel(String column1, String column2) {
		DummyModel model = new DummyModel();
		model.setColumn1(column1);
		model.setColumn2(column2);
		model.persist();
		return model;
	}
	
	@Test
	public void shouldOrderByFieldAsc() {
		DummyModel model = createModel("column1", "column2");
		DummyModel model1 = createModel("column1", "column2");
		Filter filter = new Filter();
		filter.addSortField("id", true);
		List<DummyModel> models = DummyModel.where(filter);
		assertEquals(models.get(0), model);
		assertEquals(models.get(1), model1);
	}
	
	@Test
	public void shouldOrderByFieldDesc() {
		DummyModel model = createModel("column1", "column2");
		DummyModel model1 = createModel("column1", "column2");
		Filter filter = new Filter();
		filter.addSortField("id", false);
		List<DummyModel> models = DummyModel.where(filter);
		assertEquals(models.get(1), model);
		assertEquals(models.get(0), model1);
	}
	
	@Test
	public void shouldOrderByMultipleFields() {
		DummyModel model = createModel("column1", "column2");
		DummyModel model1 = createModel("column1", "column2");
		Filter filter = new Filter();
		filter.addSortField("column1", false);
		filter.addSortField("id", false);
		List<DummyModel> models = DummyModel.where(filter);
		assertEquals(models.get(0), model1);
		assertEquals(models.get(1), model);
	}
}
