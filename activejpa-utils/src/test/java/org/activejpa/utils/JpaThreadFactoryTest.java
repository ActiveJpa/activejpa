/**
 * 
 */
package org.activejpa.utils;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.entity.testng.ActiveJpaAgentObjectFactory;
import org.activejpa.jpa.JPA;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
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
		Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
		DummyModel model1 = DummyModel.findById(model.getId());
		model.setColumn1("value4");
		model1.persist();
		thread.run();
		Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
		assertEquals(atomicLong.get(), 2L, "Assert in thread not run");
	}
}
