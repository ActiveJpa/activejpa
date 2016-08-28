/**
 * 
 */
package org.activejpa.jpa;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JPAContextTest {

	@Mock private JPAConfig config;
	
	@Mock private EntityManager entityManager;
	
	@Mock private EntityManagerFactory emf;
	
	private EntityManagerProvider emp;
	
	private JPAContext context;
	
	@BeforeMethod
	public void setup() {
		config = mock(JPAConfig.class);
		entityManager = mock(EntityManager.class);
		emf = mock(EntityManagerFactory.class);
		emp = new EntityManagerProviderImpl(emf);
		when(config.getEntityManagerProvider()).thenReturn(emp);
		when(emf.createEntityManager()).thenReturn(entityManager);
		context = new JPAContext(config, false);
	}
	
	@Test
	public void shouldGetEntityManager() {
		assertEquals(context.getEntityManager(), entityManager);
	}
	
	@Test
	public void shouldGetSameEntityManagerSecondTime() {
		context.getEntityManager();
		context.getEntityManager();
		verify(emf, times(1)).createEntityManager();
	}
	
	@Test
	public void shouldClose() {
		EntityManager entityManager = context.getEntityManager();
		when(entityManager.isOpen()).thenReturn(true);
		context.close();
		verify(entityManager).close();
	}
	
	@Test
	public void shouldNotCloseIfNotOpen() {
		EntityManager entityManager = context.getEntityManager();
		when(entityManager.isOpen()).thenReturn(false);
		context.close();
		verify(entityManager, never()).close();
	}
	
	@Test
	public void shouldSayTxnOpen() {
		mockTransaction(context, true);
		assertTrue(context.isTxnOpen());
	}
	
	@Test
	public void shouldSayTxnNotOpenOnNullTransaction() {
		EntityManager entityManager = context.getEntityManager();
		when(entityManager.getTransaction()).thenReturn(null);
		assertFalse(context.isTxnOpen());
	}
	
	@Test
	public void shouldSayTxnNotOpenWhenInactive() {
		mockTransaction(context, false);
		assertFalse(context.isTxnOpen());
	}
	
	@Test
	public void shouldRollback() {
		EntityTransaction txn = mockTransaction(context, true);
		context.closeTxn(true);
		verify(txn).rollback();
	}
	
	@Test
	public void shouldRollbackIfTransactionIsMarkedForRollback() {
		EntityTransaction txn = mockTransaction(context, true);
		when(txn.getRollbackOnly()).thenReturn(true);
		context.closeTxn(true);
		verify(txn).rollback();
	}
	
	@Test
	public void shouldRollbackIfReadOnlyisSet(){
		JPAContext context = new JPAContext(config, true);
		EntityTransaction txn = mockTransaction(context, true);
		when(txn.getRollbackOnly()).thenReturn(false);
		context.closeTxn(true);
		verify(txn).rollback();
	}
	
	@Test
	public void shouldCommit() {
		EntityTransaction txn = mockTransaction(context, true);
		context.closeTxn(false);
		verify(txn).commit();
	}
	
	@Test
	public void shouldNotCommitIfTxnIsNotOpen() {
		EntityTransaction txn = mockTransaction(context, false);
		context.closeTxn(false);
		verify(txn, never()).commit();
	}
	
	@Test
	public void shouldNotRollbackIfTxnIsNotOpen() {
		EntityTransaction txn = mockTransaction(context, false);
		context.closeTxn(false);
		verify(txn, never()).rollback();
	}
	
	@Test
	public void shouldBeginTransaction() {
		EntityTransaction txn = mockTransaction(context, false);
		context.beginTxn();
		verify(txn).begin();
	}
	
	@Test
	public void shouldNotBeginTransaction() {
		EntityTransaction txn = mockTransaction(context, true);
		context.beginTxn();
		verify(txn, never()).begin();
	}
	
	private EntityTransaction mockTransaction(JPAContext context, boolean active) {
		EntityManager entityManager = context.getEntityManager();
		EntityTransaction txn = mock(EntityTransaction.class);
		when(entityManager.getTransaction()).thenReturn(txn);
		when(txn.isActive()).thenReturn(active);
		return txn;
	}
}
