/**
 * 
 */
package org.activejpa.entity;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityCollectionTest extends BaseModelTest {
	
	private DummyModel model;
	
	private DummyModel child1;
	
	private DummyModel child2;
	
	private DummyModel child3;

	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}
	
	public void setup() throws Exception {
		super.setup();
		model = createModel("testColumn1", "testColumn2");
		child1 = createModel("testChildColumn0", "testChildColumn2");
		child2 = createModel("testChildColumn1", "testChildColumn3");
		child3 = createModel("testChildColumn1", "testChildColumn3");
		model.addChild(child1);
		model.addChild(child2);
		model.addChild(child3);
		model.persist();
	}
	
	@Test
	public void shouldFindById() {
		assertEquals(model.collection("children").findById(child2.getId()), child2);
	}
	
	@Test
	public void shouldSearchAndReturnFirst() {
		assertEquals(model.collection("children").first("children.column1", "testChildColumn1"), child2);
	}
	
	@Test
	public void shouldSearchAndReturnTheOnlyMatch() {
		assertEquals(model.collection("children").one("children.column1", "testChildColumn0", "children.column2", "testChildColumn2"), child1);
	}
	
	@Test
	public void shouldSearchByKeyValue() {
		assertEquals(model.collection("children").where("children.column1", "testChildColumn0"), Arrays.asList(child1));
	}
	
	@Test
	public void shouldSearchByMultipleKeyValues() {
		assertEquals(model.collection("children").where("children.column1", "testChildColumn0", "children.column2", "testChildColumn2"), Arrays.asList(child1));
	}
	
	@Test
	public void shouldSearchUsingFilter() {
		assertEquals(model.collection("children").where(new Filter(new Condition("children.column1", "testChildColumn0"), new Condition("children.column2", "testChildColumn2"))), Arrays.asList(child1));
	}
	
	private DummyModel createModel(String column1, String column2) {
		DummyModel model = new DummyModel();
		model.setColumn1(column1);
		model.setColumn2(column2);
		model.persist();
		return model;
	}
}
