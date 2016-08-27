/**
 * 
 */
package org.activejpa.jpa;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.persistence.EntityManagerFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JPAConfigTest {
	
	private JPAConfig config;
	
	private EntityManagerProvider emp;
	
	private EntityManagerFactory emf;

	@BeforeMethod
	public void setup() {
		emf = mock(EntityManagerFactory.class);
		emp = new EntityManagerProviderImpl(emf);
		config = new JPAConfig("test", emp);
	}
	
	@Test
	public void shouldGetContext() {
		JPAContext context = config.getContext();
		assertNotNull(context);
		assertFalse(context.isReadOnly());
	}
	
	@Test
	public void shouldGetReadOnlyContext() {
		JPAContext context = config.getContext(true);
		assertNotNull(context);
		assertTrue(context.isReadOnly());
	}
	
	@Test
	public void shouldGetReadWriteContext() {
		JPAContext context = config.getContext(false);
		assertNotNull(context);
		assertFalse(context.isReadOnly());
	}
	
	@Test
	public void shouldGetSameContextSecondTime() {
		JPAContext context = config.getContext(true);
		assertEquals(context, config.getContext());
	}
	
	@Test
	public void shouldClose() {
		config.close();
		verify(emf).close();
	}
	
	@Test
	public void shouldClearContext() {
		JPAContext context = config.getContext();
		config.clearContext();
		assertNotEquals(config.getContext(), context);
	}
}
