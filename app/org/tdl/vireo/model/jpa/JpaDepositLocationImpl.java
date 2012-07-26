package org.tdl.vireo.model.jpa;

import java.net.MalformedURLException;
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.deposit.Depositor;
import org.tdl.vireo.deposit.Packager;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.DepositLocation;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.modules.spring.Spring;

/**
 * JPA specific implementation of the Deposit Location interface.
 * 
 * This class will store all URL datatypes as strings in the database, and then
 * re-parse them when requested. Also in a similar manner when packagers and
 * depositors will be stored based upon their spring bean names. If those names
 * change then problems will occure.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "deposit_location")
public class JpaDepositLocationImpl extends JpaAbstractModel<JpaDepositLocationImpl> implements DepositLocation {

	@Column(nullable = false)
	public int displayOrder;

	@Column(nullable = false, unique = true, length=255)
	public String name;
	
	@Column(length=1024)
	public String repositoryURL;
	
	@Column(length=1024)
	public String collectionURL;
	
	@Column(length=255)
	public String username;
	
	@Column(length=255)
	public String password;
	
	@Column(length=255)
	public String onBehalfOf;

	@Column(length=255)
	public String packager;
	
	@Column(length=255)
	public String depositor;
	
	/**
	 * Construct a new JpaDepositLocation
	 * 
	 * @param name
	 *            The name of the new deposit location.
	 */
	protected JpaDepositLocationImpl(String name) {

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("Name is required");

		assertManager();
		
		this.displayOrder = 0;
		this.name = name;
	}
	
	@Override
	public JpaDepositLocationImpl save() {
		assertManager();

		return super.save();
	}
	
	@Override
	public JpaDepositLocationImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {
		
		assertManager();
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
		assertManager();

		this.name = name;
	}
	
	@Override
	public URL getRepositoryURL() {
		
		if (repositoryURL == null)
			return null;
		
		try {
			return new URL(repositoryURL);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public void setRepositoryURL(URL url) {
		
		assertManager();
		
		if (url == null)
			repositoryURL = null;
		else
			repositoryURL = url.toExternalForm();
	}

	@Override
	public URL getCollectionURL() {
		if (collectionURL == null)
			return null;
		
		try {
			return new URL(collectionURL);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public void setCollectionURL(URL url) {

		assertManager();
		
		if (url == null)
			collectionURL = null;
		else
			collectionURL = url.toExternalForm();
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		assertManager();
		
		this.username = username;
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public void setPassword(String password) {
		assertManager();
		
		this.password = password;
	}

	@Override
	public String getOnBehalfOf() {
		return onBehalfOf;
	}

	@Override
	public void setOnBehalfOf(String onBehalfOf) {
		assertManager();
		
		this.onBehalfOf = onBehalfOf;
	}

	@Override
	public Packager getPackager() {
		
		if (packager == null)
			return null;
		
		try {
			Object bean = Spring.getBean(packager);
			return (Packager) bean;
		} catch (RuntimeException re) {
			Logger.warn(re,"Unable to packager for deposit location "+id+" because of an exception");
			return null;
		}
		
	}

	@Override
	public void setPackager(Packager packager) {
		assertManager();
		
		if (packager == null) 
			this.packager = null;
		else 
			this.packager = packager.getBeanName();
	}

	@Override
	public Depositor getDepositor() {
		
		if (depositor == null)
			return null;
		
		try {
			Object bean = Spring.getBean(depositor);
			return (Depositor) bean;
		} catch (RuntimeException re) {
			Logger.warn(re,"Unable to depositor for deposit location "+id+" because of an exception");
			return null;
		}
		
	}

	@Override
	public void setDepositor(Depositor depositor) {
		assertManager();
		
		if (depositor == null) 
			this.depositor = null;
		else 
			this.depositor = depositor.getBeanName();
	}
	
}
