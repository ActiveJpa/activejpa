/**
 * 
 */
package org.activejpa.entity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ganeshs
 *
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper=false)
public class SortField extends AbstractConstruct {

	private final String name;
	
	private final boolean asc;
	
	protected <T extends Model> Order getOrder(CriteriaBuilder builder, Root<T> root) {
		Path<?> path = getPath(root, name);
		return asc ? builder.asc(path) : builder.desc(path);
	}
}
