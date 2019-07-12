/**
 * 
 */
package org.activejpa.jpa;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Collections;

import javax.persistence.EntityManagerFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JPATest {
	
	private EntityManagerFactory emf;
	
	private EntityManagerProvider emp;
	
	private JPA jpa;
	
	@BeforeMethod
	public void setup() throws Exception {
		jpa = spy(JPA.instance);
		emf = mock(EntityManagerFactory.class);
		emp = new EntityManagerProviderImpl(emf);
		doReturn(emf).when(jpa).createEntityManagerFactory("test", Collections.<String, String>emptyMap());
		doReturn(emf).when(jpa).createEntityManagerFactory("test1", Collections.<String, String>emptyMap());
	}

	@Test
	public void shouldAddPersistenceUnitUsingName() {
		jpa.addPersistenceUnit("test");
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, true);
	}
	
	@Test
	public void shouldAddDefaultPersistenceUnitUsingName() {
		jpa.addPersistenceUnit("test", true);
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, true);
	}
	
	@Test
	public void shouldAddNonDefaultPersistenceUnitUsingName() {
		jpa.addPersistenceUnit("test", false);
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, false);
	}
	
	@Test
	public void shouldAddEntityManagerFactory() {
		jpa.addPersistenceUnit("test", emf);
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, true);
	}
	
	@Test
	public void shouldAddDefaultEntityManagerFactory() {
		jpa.addPersistenceUnit("test", emf, true);
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, true);
	}
	
	@Test
	public void shouldAddPersistenceUnitWithProperties() {
		jpa.addPersistenceUnit("test", Collections.<String, String>emptyMap(), false);
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, false);
	}
	
	@Test
	public void shouldAddDefaultPersistenceUnitWithProperties() {
		jpa.addPersistenceUnit("test", Collections.<String, String>emptyMap(), true);
		JPAConfig config = jpa.getConfig("test");
		assertDefaultConfig(config, true);
	}
	
	@Test
	public void shouldClose() {
		when(emf.isOpen()).thenReturn(true);
		jpa.addPersistenceUnit("test");
		jpa.addPersistenceUnit("test1");
		jpa.close();
		assertNull(jpa.getConfig("test"));
		assertNull(jpa.getConfig("test1"));
		verify(emf, times(2)).close();
	}
	
	private void assertDefaultConfig(JPAConfig config, boolean isDefault) {
		assertNotNull(config);
		if(isDefault) {
			assertEquals(config, jpa.getDefaultConfig());
		} else {
			assertNotEquals(config, jpa.getDefaultConfig());
		}
		assertEquals(config.getEntityManagerProvider().getEntityManagerFactory(), emf);
	}
}
