/**
 * 
 */
package org.activejpa.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityTransaction;

import org.activejpa.entity.testng.ActiveJpaAgentObjectFactory;
import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 * @author ganeshs
 *
 */
public class JpaThreadFactoryTest extends BaseModelTest {
	
	/**
	 * HACK. `mvn test` will be run before the package is created. javaagent can be loaded only from a jar. Since the
	 * jar is not yet created, it will throw agent not found exception. This is a hack to get rid of that exception
	 */
	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
		return new ActiveJpaAgentObjectFactory();
	}
	
	@BeforeClass
	public void beforeClass() throws Exception {
		JPA.instance.addPersistenceUnit("test");
	}
	
	@Override
	public void setup() throws Exception {
	}
	
	@Override
	public void destroy() throws Exception {
		DummyModel.deleteAll();
	}
	
	@Test
	public void shouldCreateNewEntityManagerForEveryThreadRun() {
		final DummyModel model = new DummyModel("value1", "value2", "value3");
		model.persist();
		final AtomicLong atomicLong = new AtomicLong(0);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				DummyModel model1 = DummyModel.findById(model.getId());
				if (atomicLong.get() == 1) {
					assertEquals(model1.getColumn1(), "value1");
					atomicLong.incrementAndGet();
				}
			}
		};
		Thread thread = new ActiveJpaThreadFactory().newThread(runnable);
		thread.run();
		atomicLong.incrementAndGet();
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		DummyModel model1 = DummyModel.findById(model.getId());
		model.setColumn1("value4");
		model1.persist();
		thread.run();
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		assertEquals(atomicLong.get(), 2L, "Assert in thread not run");
	}
	
	@Test
	public void shouldCloseOpenTransaction() {
		final DummyModel model = new DummyModel("value1", "value2", "value3");
		model.persist();
		final AtomicReference<EntityTransaction> transaction = new AtomicReference<>();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				DummyModel model1 = DummyModel.findById(model.getId());
				transaction.set(model1.beginTxn());
				assertTrue(transaction.get().isActive());
			}
		};
		Thread thread = new ActiveJpaThreadFactory().newThread(runnable);
		thread.run();
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		assertFalse(transaction.get().isActive());
	}
	
	@Test
	public void shouldCloseOpenTransactionOnException() {
		final DummyModel model = new DummyModel("value1", "value2", "value3");
		model.persist();
		final AtomicReference<EntityTransaction> transaction = new AtomicReference<>();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				transaction.set(DummyModel.beginTxn());
				throw new RuntimeException();
			}
		};
		Thread thread = new ActiveJpaThreadFactory().newThread(runnable);
		thread.run();
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		assertFalse(transaction.get().isActive());
	}
	
	@Test
	public void shouldCloseContext() {
		final DummyModel model = new DummyModel("value1", "value2", "value3");
		model.persist();
		final AtomicReference<JPAContext> context = new AtomicReference<>();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				context.set(JPA.instance.getDefaultConfig().getContext());
			}
		};
		Thread thread = new ActiveJpaThreadFactory().newThread(runnable);
		thread.run();
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		assertNotEquals(context.get(), JPA.instance.getDefaultConfig().getContext());
	}
	
	@Test
	public void shouldCloseContextOnException() {
		final DummyModel model = new DummyModel("value1", "value2", "value3");
		model.persist();
		final AtomicReference<JPAContext> context = new AtomicReference<>();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				context.set(JPA.instance.getDefaultConfig().getContext());
				throw new RuntimeException();
			}
		};
		Thread thread = new ActiveJpaThreadFactory().newThread(runnable);
		thread.run();
		Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
		assertNotEquals(context.get(), JPA.instance.getDefaultConfig().getContext());
	}
}
