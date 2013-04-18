/**
 * 
 */
package org.mockito.configuration;


/**
 * @author ganeshs
 *
 */
public class MockitoConfiguration extends DefaultMockitoConfiguration {
	
	public MockitoConfiguration() {
		System.out.println("test");
	}

	@Override
	public boolean enableClassCache() {
		return false;
	}

}
