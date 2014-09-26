/**
 * 
 */
package org.activejpa.enhancer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.instrument.IllegalClassFormatException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class DomainClassFileTransformerTest {

	private DomainClassEnhancer enhancer;
	
	@BeforeMethod
	public void setup() {
		enhancer = mock(DomainClassEnhancer.class);
	}
	
	@Test
	public void shouldTransform() throws IllegalClassFormatException {
		ClassLoader loader = mock(ClassLoader.class);
		DomainClassFileTransformer transformer = new DomainClassFileTransformer(enhancer);
		transformer.transform(loader, DomainClassEnhancer.class.getName(), null, null, null);
		verify(enhancer).enhance(loader, DomainClassEnhancer.class.getName());
	}
	
	@Test
	public void shouldNotTransformIgnoredPackageClasses() throws IllegalClassFormatException {
		ClassLoader loader = mock(ClassLoader.class);
		DomainClassFileTransformer transformer = new DomainClassFileTransformer(enhancer);
		transformer.transform(loader, "javax/test", null, null, null);
		transformer.transform(loader, "java/lang/String", null, null, null);
		transformer.transform(loader, "com/sun/test", null, null, null);
		transformer.transform(loader, "sun/test", null, null, null);
		verify(enhancer, never()).enhance(eq(loader), anyString());
	}
}
