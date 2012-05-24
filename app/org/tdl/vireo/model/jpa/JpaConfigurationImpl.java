package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Configuration;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Jpa specific implementation of Vireo's Configuration interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Configuration")
public class JpaConfigurationImpl extends JpaAbstractModel<JpaConfigurationImpl> implements Configuration {

	@Column(nullable = false, unique = true)
	public String name;

	public String value;

	/**
	 * Construct a new JpaConfigurationImpl
	 * 
	 * @param name
	 *            The name of the configuration parameter.
	 * @param value
	 *            The value of the configuration parameter.
	 */
	protected JpaConfigurationImpl(String name, String value) {
	
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.name = name;
		this.value = value;
	}
	
	@Override
	public JpaConfigurationImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaConfigurationImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");
		
		assertManager();
		
		this.name = name;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		
		assertManager();
		this.value = value;
	}

}
