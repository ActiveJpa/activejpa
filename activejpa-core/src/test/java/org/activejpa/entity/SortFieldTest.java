/**
 * 
 */
package org.activejpa.entity;

import static org.testng.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SortFieldTest {
	
	private CriteriaBuilder builder;
	
	private Root root;
	
	private Path path1;
	
	private Order order1, order2, order3, order4;
	
	@BeforeMethod
	public void setup() {
		builder = mock(CriteriaBuilder.class);
		root = mock(Root.class);
		when(root.get("field1")).thenReturn(path1);
		when(builder.asc(path1)).thenReturn(order1);
		when(builder.desc(path1)).thenReturn(order2);
	}


	@Test
	public void shouldGetAscOrder() {
		SortField sortField = new SortField("field1", true);
		assertEquals(sortField.getOrder(builder, root), order1);
	}
	
	@Test
	public void shouldGetDescOrder() {
		SortField sortField = new SortField("field1", false);
		assertEquals(sortField.getOrder(builder, root), order2);
	}
}
