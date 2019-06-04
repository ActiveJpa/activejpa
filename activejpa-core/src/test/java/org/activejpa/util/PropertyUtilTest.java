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

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.activejpa.ActiveJpaException;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

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
	
	@Test
	public void shouldCheckIfSimpleValueType() {
		assertTrue(PropertyUtil.isSimpleValueType(Integer.class));
		assertTrue(PropertyUtil.isSimpleValueType(DummyClass.Enum.class));
		assertTrue(PropertyUtil.isSimpleValueType(BigInteger.class));
		assertTrue(PropertyUtil.isSimpleValueType(Date.class));
		assertTrue(PropertyUtil.isSimpleValueType(String.class));
		assertTrue(PropertyUtil.isSimpleValueType(URI.class));
		assertTrue(PropertyUtil.isSimpleValueType(URL.class));
		assertTrue(PropertyUtil.isSimpleValueType(Locale.class));
		assertTrue(PropertyUtil.isSimpleValueType(Class.class));
		assertTrue(PropertyUtil.isSimpleValueType(Serializable.class));
		assertTrue(PropertyUtil.isSimpleValueType(Timestamp.class));
		assertFalse(PropertyUtil.isSimpleValueType(DummyClass.class));
	}
	
	@Test
	public void shouldCheckIfSimpleProperty() {
		DummyClass clazz = new DummyClass();
		assertTrue(PropertyUtil.isSimpleProperty(String.class));
		assertFalse(PropertyUtil.isSimpleProperty(clazz.objectArray.getClass()));
		assertTrue(PropertyUtil.isSimpleProperty(clazz.stringArray.getClass()));
		assertFalse(PropertyUtil.isSimpleProperty(clazz.objectList.getClass()));
		assertFalse(PropertyUtil.isSimpleProperty(clazz.stringList.getClass()));
	}
	
	@Test
	public void shouldCheckIfCollectionPropertyWithClass() {
		assertTrue(PropertyUtil.isCollectionProperty(ArrayList.class, false));
		assertTrue(PropertyUtil.isCollectionProperty(HashSet.class, false));
		assertFalse(PropertyUtil.isCollectionProperty(String.class, false));
		assertFalse(PropertyUtil.isCollectionProperty(HashMap.class, false));
	}
	
	@Test
	public void shouldCheckIfCollectionPropertyForMap() {
		assertTrue(PropertyUtil.isCollectionProperty(HashMap.class, true));
		assertFalse(PropertyUtil.isCollectionProperty(String.class, true));
	}
	
	@Test
	public void shouldCheckCollectionPropertyWithType() throws Exception {
		assertTrue(PropertyUtil.isCollectionProperty(DummyClass.class.getField("stringList").getGenericType(), false));
		assertTrue(PropertyUtil.isCollectionProperty(DummyClass.class.getField("objectList").getGenericType(), false));
		assertTrue(PropertyUtil.isCollectionProperty(new HashSet<String>().getClass(), false));
		assertTrue(PropertyUtil.isCollectionProperty(new HashSet<DummyClass>().getClass(), false));
		assertFalse(PropertyUtil.isCollectionProperty(DummyClass.class.getField("boxed").getGenericType(), false));
		assertFalse(PropertyUtil.isCollectionProperty(String.class, false));
		assertFalse(PropertyUtil.isCollectionProperty(HashMap.class, false));
		assertFalse(PropertyUtil.isCollectionProperty(DummyClass.class.getField("genericObjectArray").getGenericType(), false));
	}
	
	@Test
	public void shouldCheckIfMapPropertyWithClass() {
		assertTrue(PropertyUtil.isMapProperty(HashMap.class));
		assertFalse(PropertyUtil.isMapProperty(List.class));
	}
	
	@Test
	public void shouldCheckIfMapPropertyWithType() throws Exception {
		assertTrue(PropertyUtil.isMapProperty(DummyClass.class.getField("stringMap").getGenericType()));
		assertFalse(PropertyUtil.isMapProperty(DummyClass.class.getField("genericObjectArray").getGenericType()));
	}
	
	@Test
	public void shouldGetCollectionType() throws Exception {
		assertEquals(PropertyUtil.getCollectionElementType(DummyClass.class.getField("stringList").getGenericType()), String.class);
		assertEquals(PropertyUtil.getCollectionElementType(DummyClass.class.getField("objectList").getGenericType()), DummyClass.class);
		assertEquals(PropertyUtil.getCollectionElementType(DummyClass.class.getField("anyList").getGenericType()), Object.class);
	}
	
	@Test
	public void shouldGetCollectionTypeForMap() throws Exception {
		assertEquals(PropertyUtil.getCollectionElementType(DummyClass.class.getField("stringMap").getGenericType()), String.class);
		assertEquals(PropertyUtil.getCollectionElementType(DummyClass.class.getField("genericType").getGenericType()), Object.class);
	}
	
	public static class Box<T> {
	}
	
	public static class DummyClass<T> {
		
		public enum Enum {
			value1, value2
		}
		
		public int primitiveInt;
		
		public Integer wrapperInt;
		
		public Object object;
		
		public Object fieldWithGetter;
		
		public Object fieldWithoutGetter;
		
		public String[] stringArray = new String[] {};
		
		public DummyClass[] objectArray = new DummyClass[] {};
		
		public T[] genericObjectArray;
		
		public List<String> stringList = Lists.newArrayList();
		
		public List<DummyClass> objectList = Lists.newArrayList();
		
		public List anyList;
		
		public Map<String, String> stringMap;
		
		public Box<String> boxed;
		
		public DummyClass<String> genericType;

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
