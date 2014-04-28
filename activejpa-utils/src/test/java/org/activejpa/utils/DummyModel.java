/**
 * 
 */
package org.activejpa.utils;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;

/**
 * @author ganeshs
 *
 */
@Entity
public class DummyModel extends Model {
	
	private Long id;
	
	private String column1;
	
	private String column2;
	
	private String column3;
	
	private Set<DummyModel> children = new HashSet<DummyModel>();
	
	private DummyModel parent;

	public DummyModel() {
	}

	/**
	 * @param column1
	 * @param column2
	 * @param column3
	 */
	public DummyModel(String column1, String column2, String column3) {
		this.column1 = column1;
		this.column2 = column2;
		this.column3 = column3;
	}

	/**
	 * @return the parent
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="parent_id")
	public DummyModel getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(DummyModel parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	public Set<DummyModel> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Set<DummyModel> children) {
		this.children = children;
	}
	
	public void addChild(DummyModel child) {
		child.parent = this;
		this.children.add(child);
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public String getColumn1() {
		return column1;
	}

	public void setColumn1(String column1) {
		this.column1 = column1;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}

	public String getColumn3() {
		return column3;
	}

	public void setColumn3(String column3) {
		this.column3 = column3;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DummyModel other = (DummyModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
