package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.College;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's College interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "College")
public class JpaCollegeImpl extends JpaAbstractModel<JpaCollegeImpl> implements College {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true)
	public String name;

	/**
	 * Construct a new JpaCollegeImpl
	 * 
	 * @param name
	 *            The name of the new college.
	 */
	protected JpaCollegeImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		this.displayOrder = 0;
		this.name = name;
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		this.name = name;
	}

}
