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
	}

	@Override
	public boolean enableClassCache() {
		return false;
	}

}
