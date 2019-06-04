/**
 * 
 */
package org.activejpa.enhancer;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

/**
 * @author ganesh.s
 *
 */
public class ActiveJpaAgentLoaderTest {

	@Test
	public void shouldGetLoaderInstance() {
		assertNotNull(ActiveJpaAgentLoader.instance());
	}
}
