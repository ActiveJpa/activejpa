/**
 * 
 */
package org.activejpa.entity;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author ganeshs
 *
 */
public class ModelTest extends BaseModelTest {
	
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
	
	private DummyModel createModel(String column1, String column2) {
		DummyModel model = new DummyModel();
		model.setColumn1(column1);
		model.setColumn2(column2);
		model.persist();
		return model;
	}
}
