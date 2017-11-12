package org.activejpa.entity;
/**
 * 
 */


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.List;

import org.activejpa.entity.Condition;
import org.activejpa.entity.Filter;
import org.activejpa.entity.Model;
import org.activejpa.entity.ModelInterceptor;
import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.entity.testng.DomainClassObjectFactory;
import org.activejpa.jpa.JPA;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class ModelInterceptorTest extends BaseModelTest {
    
    /**
     * HACK. `mvn test` will be run before the package is created. javaagent can be loaded only from a jar. Since the
     * jar is not yet created, it will throw agent not found exception. This is a hack to get rid of that exception
     */
    @ObjectFactory
    public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
        return new DomainClassObjectFactory();
    }
    
    @BeforeClass
    public void beforeClass() {
        JPA.instance.addPersistenceUnit("test");
    }
    
    @Test
    public void shouldCallAll() throws Throwable {
        DummyModel dummyModel = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        List<Model> models = ModelInterceptor.all(DummyModel.class.getMethod("all"));
        assertEquals(models.size(), 1);
        assertEquals(models.get(0), dummyModel);
    }
    
    @Test
    public void shouldCallWhereWithParams() throws Throwable {
        DummyModel dummyModel1 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        List<Model> models = ModelInterceptor.where(DummyModel.class.getMethod("where", Object[].class), new Object[] {"column1", dummyModel1.getColumn1()});
        assertEquals(models.size(), 1);
        assertEquals(models.get(0), dummyModel1);
    }
    
    @Test
    public void shouldCallWhereWithFilter() throws Throwable {
        DummyModel dummyModel1 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        List<Model> models = ModelInterceptor.where(DummyModel.class.getMethod("where", Filter.class), new Filter(new Condition("column1", dummyModel1.getColumn1())));
        assertEquals(models.size(), 1);
        assertEquals(models.get(0), dummyModel1);
    }
    
    @Test
    public void shouldCallCountWithFilter() throws Throwable {
        DummyModel dummyModel1 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        assertEquals(ModelInterceptor.count(DummyModel.class.getMethod("count", Filter.class), new Filter(new Condition("column1", dummyModel1.getColumn1()))), 1L);
    }
    
    @Test
    public void shouldCallCount() throws Throwable {
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        assertEquals(ModelInterceptor.count(DummyModel.class.getMethod("count")), 2L);
    }
    
    @Test
    public void shouldCallDeleteAllWithFilter() throws Throwable {
        DummyModel dummyModel1 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        ModelInterceptor.deleteAll(DummyModel.class.getMethod("deleteAll", Filter.class), new Filter(new Condition("column1", dummyModel1.getColumn1())));
        assertEquals(DummyModel.count(), 1L);
    }
    
    @Test
    public void shouldCallDeleteAll() throws Throwable {
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        ModelInterceptor.deleteAll(DummyModel.class.getMethod("deleteAll"));
        assertEquals(DummyModel.count(), 0L);
    }
    
    @Test
    public void shouldCallFindById() throws Throwable {
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        DummyModel model2 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        assertEquals(ModelInterceptor.findById(DummyModel.class.getMethod("findById", Serializable.class), model2.getId()), model2);
    }
    
    @Test
    public void shouldCallExists() throws Throwable {
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        DummyModel model2 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        assertTrue(ModelInterceptor.exists(DummyModel.class.getMethod("exists", Serializable.class), model2.getId()));
    }
    
    @Test
    public void shouldCallFirstWithParams() throws Throwable {
        DummyModel dummyModel1 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        assertEquals(ModelInterceptor.first(DummyModel.class.getMethod("first", Object[].class), new Object[] {"column1", dummyModel1.getColumn1()}), dummyModel1);
    }
    
    @Test
    public void shouldCallOneWithParams() throws Throwable {
        DummyModel dummyModel1 = createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        createModel(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));
        assertEquals(ModelInterceptor.one(DummyModel.class.getMethod("one", Object[].class), new Object[] {"column1", dummyModel1.getColumn1()}), dummyModel1);
    }

    private DummyModel createModel(String column1, String column2) {
        DummyModel model = new DummyModel();
        model.setColumn1(column1);
        model.setColumn2(column2);
        model.persist();
        return model;
    }
}
