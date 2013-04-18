/**
 * 
 */
package org.activejpa.enhancer;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.activejpa.entity.DummyModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class DomainClassEnhancerTest {
	
	private ClassPool classPool;
	
	private DomainClassEnhancer enhancer;
	
	@BeforeMethod
	public void setup() throws NotFoundException {
		this.classPool = spy(ClassPool.getDefault());
		this.enhancer = new DomainClassEnhancer(classPool);
	}
	
	@Test
	public void shouldAllowEnhancingForDomainModel() {
		assertTrue(enhancer.canEnhance(DummyModel.class.getName()));
	}
	
	@Test
	public void shouldNotAllowEnhancingForNonModel() {
		assertFalse(enhancer.canEnhance(String.class.getName()));
	}
	
	@Test
	public void shouldNotAllowEnhancingForNonEntity() {
		assertFalse(enhancer.canEnhance(String.class.getName()));
	}
	
	@Test
	public void shouldAddMethodsToDomainClass() throws Exception {
		CtClass ctClass = classPool.get(DummyModel.class.getName());
		doReturn(ctClass).when(classPool).get(DummyModel.class.getName());
		enhancer.enhance(this.getClass().getClassLoader(), DummyModel.class.getName().replace(".", "/"));
		assertNotNull(ctClass.getMethod("findById", "(Ljava/io/Serializable;)Lorg/activejpa/entity/Model;"));
		assertNotNull(ctClass.getMethod("count", "()J"));
		assertNotNull(ctClass.getMethod("count", "(Lorg/activejpa/entity/Filter;)J"));
		assertNotNull(ctClass.getMethod("all", "()Ljava/util/List;"));
		assertNotNull(ctClass.getMethod("one", "([Ljava/lang/Object;)Lorg/activejpa/entity/Model;"));
		assertNotNull(ctClass.getMethod("first", "([Ljava/lang/Object;)Lorg/activejpa/entity/Model;"));
		assertNotNull(ctClass.getMethod("deleteAll", "()V"));
		assertNotNull(ctClass.getMethod("deleteAll", "(Lorg/activejpa/entity/Filter;)V"));
		assertNotNull(ctClass.getMethod("where", "([Ljava/lang/Object;)Ljava/util/List;"));
		assertNotNull(ctClass.getMethod("where", "(Lorg/activejpa/entity/Filter;)Ljava/util/List;"));
		assertNotNull(ctClass.getMethod("exists", "(Ljava/io/Serializable;)Z"));
	}

}
