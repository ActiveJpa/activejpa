/**
 * 
 */
package org.activejpa.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.activejpa.ActiveJpaException;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class PropertyUtilTest {

	@Test
	public void shouldReturnTrueIfTypeIsPrimitiveOrWrapper() throws SecurityException, NoSuchFieldException {
		assertTrue(PropertyUtil.isPrimitiveOrWrapper(DummyClass.class.getField("primitiveInt").getType()));
	}
	
	@Test
	public void shouldReturnFalseIfTypeIsNotPrimitiveOrNotWrapper() throws SecurityException, NoSuchFieldException {
		assertFalse(PropertyUtil.isPrimitiveOrWrapper(DummyClass.class.getField("object").getType()));
	}
	
	@Test
	public void shouldGetReadMethod() {
		DummyClass clazz = new DummyClass();
		assertNotNull(PropertyUtil.getReadMethod(clazz, "fieldWithGetter"));
	}
	
	@Test
	public void shouldNotGetReadMethod() {
		DummyClass clazz = new DummyClass();
		try {
			PropertyUtil.getReadMethod(clazz, "fieldWithoutGetter");
			fail("Expected exception but got none");
		} catch (ActiveJpaException e) {
		}
	}
	
	@Test
	public void shouldSetPropertyWithValue() {
		DummyClass clazz = new DummyClass();
		PropertyUtil.setProperty(clazz, "wrapperInt", 100);
		assertEquals(clazz.getWrapperInt(), Integer.valueOf(100));
	}
	
	@Test
	public void shouldGetPropertyValue() {
		DummyClass clazz = new DummyClass();
		clazz.wrapperInt = 100;
		assertEquals(PropertyUtil.getProperty(clazz, "wrapperInt"), 100);
	}
	
	@Test
	public void shouldNotGetPropertyValue() {
		DummyClass clazz = new DummyClass();
		assertNull(PropertyUtil.getProperty(clazz, "nonExistingField"));
	}
	
	public static class DummyClass {
		
		public int primitiveInt;
		
		public Integer wrapperInt;
		
		public Object object;
		
		public Object fieldWithGetter;
		
		public Object fieldWithoutGetter;

		/**
		 * @return
		 */
		public Object getFieldWithGetter() {
			return fieldWithGetter;
		}

		/**
		 * @return the primitiveInt
		 */
		public int getPrimitiveInt() {
			return primitiveInt;
		}

		/**
		 * @param primitiveInt the primitiveInt to set
		 */
		public void setPrimitiveInt(int primitiveInt) {
			this.primitiveInt = primitiveInt;
		}

		/**
		 * @return the wrapperInt
		 */
		public Integer getWrapperInt() {
			return wrapperInt;
		}

		/**
		 * @param wrapperInt the wrapperInt to set
		 */
		public void setWrapperInt(Integer wrapperInt) {
			this.wrapperInt = wrapperInt;
		}

		/**
		 * @return the object
		 */
		public Object getObject() {
			return object;
		}

		/**
		 * @param object the object to set
		 */
		public void setObject(Object object) {
			this.object = object;
		}

		/**
		 * @param fieldWithGetter the fieldWithGetter to set
		 */
		public void setFieldWithGetter(Object fieldWithGetter) {
			this.fieldWithGetter = fieldWithGetter;
		}
	}
}
