/**
 * 
 */
package org.activejpa.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class BeanUtilTest {
	
	private Map<String, Object> model3;
	
	@BeforeMethod
	public void setup() {
		model3 = new HashMap<String, Object>();
		Map<String, Object> model1 = new HashMap<String, Object>();
		model1.put("stringValue", "string");
		model1.put("integerValue", "1234");
		Map<String, Object> model2 = new HashMap<String, Object>();
		model2.put("model", model1);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test123", model2);
		model3.put("map", map);
	}
	
	@Test
	public void shouldLoadBean() {
		DummyModel3 model3 = new DummyModel3();
		BeanUtil.load(model3, this.model3);
		Map<String, DummyModel2> map = model3.getMap();
		assertTrue(map.containsKey("test123"));
		assertNotNull(map.get("test123").getModel());
		assertEquals(map.get("test123").getModel().getIntegerValue(), new Integer(1234));
		assertEquals(map.get("test123").getModel().getStringValue(), "string");
	}
	
	@Test
	public void shouldLoadBeanWithMapOfStrings() {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		data.put("stringValue", "string");
		data.put("integerValue", "1234");
		map.put("map", data);
		
		DummyModel2 model2 = new DummyModel2();
		BeanUtil.load(model2, map);
		assertTrue(model2.getMap().containsKey("stringValue"));
	}

	public static class DummyModel1 {
		
		private String stringValue;
		
		private Integer integerValue;

		/**
		 * @return the stringValue
		 */
		public String getStringValue() {
			return stringValue;
		}

		/**
		 * @param stringValue the stringValue to set
		 */
		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}

		/**
		 * @return the integerValue
		 */
		public Integer getIntegerValue() {
			return integerValue;
		}

		/**
		 * @param integerValue the integerValue to set
		 */
		public void setIntegerValue(Integer integerValue) {
			this.integerValue = integerValue;
		}
		
		
	}
	
	public static class DummyModel2 {
		
		private DummyModel1 model;
		
		private Map<String, String> map = new HashMap<String, String>();

		/**
		 * @return the model
		 */
		public DummyModel1 getModel() {
			return model;
		}

		/**
		 * @param model the model to set
		 */
		public void setModel(DummyModel1 model) {
			this.model = model;
		}

		/**
		 * @return the map
		 */
		public Map<String, String> getMap() {
			return map;
		}

		/**
		 * @param map the map to set
		 */
		public void setMap(Map<String, String> map) {
			this.map = map;
		}
	}
	
	public static class DummyModel3 {
		
		private Map<String, DummyModel2> map = new HashMap<String, DummyModel2>();

		/**
		 * @return the map
		 */
		public Map<String, DummyModel2> getMap() {
			return map;
		}

		/**
		 * @param map the map to set
		 */
		public void setMap(Map<String, DummyModel2> map) {
			this.map = map;
		}
	}
}
