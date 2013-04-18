/**
 * 
 */
package org.activejpa;

import org.activejpa.entity.testng.DomainClassObjectFactory;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.ObjectFactory;

/**
 * @author ganeshs
 *
 */
public class BaseTest {
	
	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
		return new DomainClassObjectFactory();
	}
}
