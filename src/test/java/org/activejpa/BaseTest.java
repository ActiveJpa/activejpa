/**
 * 
 */
package org.activejpa;

import org.activejpa.entity.DomainClassObjectFactory;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.ObjectFactory;

/**
 * @author ganeshs
 *
 */
public class BaseTest {
	
	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) {
		return new DomainClassObjectFactory();
	}
}
