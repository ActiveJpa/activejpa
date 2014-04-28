/**
 * 
 */
package org.activejpa.entity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * @author ganeshs
 *
 */
public class SortField extends AbstractConstruct {

	private String name;
	
	private boolean asc;

	/**
	 * @param name
	 * @param asc
	 */
	public SortField(String name, boolean asc) {
		this.name = name;
		this.asc = asc;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the asc
	 */
	public boolean isAsc() {
		return asc;
	}

	/**
	 * @param asc the asc to set
	 */
	public void setAsc(boolean asc) {
		this.asc = asc;
	}
	
	/**
	 * Returns the order criteria
	 * 
	 * @param builder
	 * @param root
	 * @return
	 */
	public <T extends Model> Order getOrder(CriteriaBuilder builder, Root<T> root) {
		Path<?> path = getPath(root, name);
		return asc ? builder.asc(path) : builder.desc(path);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (asc ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortField other = (SortField) obj;
		if (asc != other.asc)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
