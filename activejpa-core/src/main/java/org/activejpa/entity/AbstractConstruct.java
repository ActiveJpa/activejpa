/**
 * 
 */
package org.activejpa.entity;

import java.util.Set;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractConstruct {

	protected <T, S> Path<?> getPath(From<T, S> root, String name) {
		int index = name.indexOf(".");
		if (index > 0 ) {
			String attribute = name.substring(0, index);
			From<S, ?> join = getJoin(attribute, root.getJoins());
			if (join == null) {
				join = root.join(attribute);
			}
			return getPath(join, name.substring(index + 1));
		} else {
			return root.get(name);
		}
	}
	
	private <T> Join<T, ?> getJoin(String name, Set<Join<T, ?>> joins) {
		for (Join<T, ?> join : joins) {
			if (join.getAttribute().getName().equals(name)) {
				return join;
			}
		}
		return null;
	}
}
