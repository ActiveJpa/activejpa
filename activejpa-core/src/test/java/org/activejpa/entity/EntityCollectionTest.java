/**
 * 
 */
package org.activejpa.entity;

import static org.testng.Assert.assertEquals;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	public void shouldGetCount() {
		assertEquals(model.collection("children").count(new Filter()), 3);
	}
	
	@Test
	public void shouldFindById() {
		assertEquals(model.collection("children").findById(child2.getId()), child2);
	}
	
	@Test
	public void should1SearchAndReturnFirst() {
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
	
	@Test
	public void shouldAddItemToCollectionUsingAddMethod() {
		ParentWithAddMethod parent = new ParentWithAddMethod();
		EntityCollection<DummyModel> collection = new EntityCollection<DummyModel>(parent, "models", DummyModel.class);
		collection.add(model);
		assertEquals(parent.models.size(), 1);
	}
	
	@Test
	public void shouldAddItemToCollectionUsingGetter() {
		ParentWithGetter parent = new ParentWithGetter();
		EntityCollection<DummyModel> collection = new EntityCollection<DummyModel>(parent, "models", DummyModel.class);
		collection.add(model);
		assertEquals(parent.models.size(), 1);
	}
	
	@Test
	public void shouldAddItemToCollectionUsingField() {
		ParentWithField parent = new ParentWithField();
		EntityCollection<DummyModel> collection = new EntityCollection<DummyModel>(parent, "models", DummyModel.class);
		collection.add(model);
		assertEquals(parent.models.size(), 1);
	}
	
	@Test
	public void shouldRemoveItemFromCollectionUsingRemoveMethod() {
		ParentWithAddMethod parent = new ParentWithAddMethod();
		EntityCollection<DummyModel> collection = new EntityCollection<DummyModel>(parent, "models", DummyModel.class);
		collection.add(model);
		assertEquals(parent.models.size(), 1);
		collection.remove(model);
		assertEquals(parent.models.size(), 0);
	}
	
	@Test
	public void shouldRemoveItemFromCollectionUsingGetter() {
		ParentWithGetter parent = new ParentWithGetter();
		EntityCollection<DummyModel> collection = new EntityCollection<DummyModel>(parent, "models", DummyModel.class);
		collection.add(model);
		assertEquals(parent.models.size(), 1);
		collection.remove(model);
		assertEquals(parent.models.size(), 0);
	}
	
	@Test
	public void shouldRemoveItemFromCollectionUsingField() {
		ParentWithField parent = new ParentWithField();
		EntityCollection<DummyModel> collection = new EntityCollection<DummyModel>(parent, "models", DummyModel.class);
		collection.add(model);
		assertEquals(parent.models.size(), 1);
		collection.remove(model);
		assertEquals(parent.models.size(), 0);
	}
	
	private DummyModel createModel(String column1, String column2) {
		DummyModel model = new DummyModel();
		model.setColumn1(column1);
		model.setColumn2(column2);
		model.persist();
		return model;
	}
	
	public static class ParentWithAddMethod extends Model {
		
		private Set<DummyModel> models = new HashSet<DummyModel>();
		
		public void addModel(DummyModel model) {
			models.add(model);
		}
		
		public void removeModel(DummyModel model) {
			models.remove(model);
		}

		@Override
		public Serializable getId() {
			return null;
		}
	}
	
	public static class ParentWithGetter extends Model {
		
		private Set<DummyModel> models = new HashSet<DummyModel>();
		
		@Override
		public Serializable getId() {
			return null;
		}

		/**
		 * @return the models
		 */
		public Set<DummyModel> getModels() {
			return models;
		}

		/**
		 * @param models the models to set
		 */
		public void setModels(Set<DummyModel> models) {
			this.models = models;
		}
	}
	
	public static class ParentWithField extends Model {
		
		private Set<DummyModel> models = new HashSet<DummyModel>();
		
		@Override
		public Serializable getId() {
			return null;
		}
	}
}
