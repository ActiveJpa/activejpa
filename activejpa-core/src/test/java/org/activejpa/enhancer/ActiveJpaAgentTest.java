/**
 * 
 */
package org.activejpa.enhancer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import org.testng.annotations.Test;

/**
 * @author ganesh.s
 *
 */
public class ActiveJpaAgentTest {

	@Test
	public void shouldAddTransformerOnPremain() throws Exception {
		Instrumentation inst = mock(Instrumentation.class);
		ActiveJpaAgent.premain("someargs", inst);
		verify(inst).addTransformer(any(ClassFileTransformer.class), eq(true));
	}
	
	@Test
	public void shouldAddTransformerOnAgentmain() throws Exception {
		Instrumentation inst = mock(Instrumentation.class);
		ActiveJpaAgent.agentmain("someargs", inst);
		verify(inst).addTransformer(any(ClassFileTransformer.class), eq(true));
	}
}
