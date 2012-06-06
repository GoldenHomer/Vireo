package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;

/**
 * This is a simple mock person class that may be useful for testing. Feel free
 * to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockPerson extends AbstractMock implements Person {

	/* Cached copies of mocked person types */
	public static MockPerson cachedAdministrator = getAdministrator();
	public static MockPerson cachedManager = getManager();
	public static MockPerson cachedReviewer = getReviewer();
	public static MockPerson cachedStudent = getStudent();

	/* Person Properties */
	public String netid;
	public String email;
	public String password;
	public String firstName;
	public String lastName;
	public String middleName;
	public String displayName;
	public Integer birthYear;
	public String currentPhoneNumber;
	public String currentPostalAddress;
	public String currentEmailAddress;
	public String permanentPhoneNumber;
	public String permanentPostalAddress;
	public String permanentEmailAddress;
	public String currentDepartment;
	public String currentCollege;
	public String currentMajor;
	public Integer currentGraduationYear;
	public Integer currentGraduationMonth;
	public List<Preference> preferences = new ArrayList<Preference>();
	public RoleType role;
	
	/**
	 * @return A new mock person with the role ADMINISTRATOR.
	 */
	public static MockPerson getAdministrator() {
		
		if (cachedAdministrator != null)
			return cachedAdministrator;
		
		MockPerson person = new MockPerson();
		person.firstName="Mock";
		person.lastName="Administrator";
		person.email="test-admin@vireo.tdl.org";
		person.role=RoleType.ADMINISTRATOR;
		return person;
	}

	/**
	 * @return A new mock person with the role MANAGER.
	 */
	public static MockPerson getManager() {
		
		if (cachedManager != null)
			return cachedManager;
		
		MockPerson person = new MockPerson();
		person.firstName="Mock";
		person.lastName="Manager";
		person.email="test-manager@vireo.tdl.org";
		person.role=RoleType.MANAGER;
		return person;
	}
	
	/**
	 * @return A new mock person with the role REVIEWER.
	 */
	public static MockPerson getReviewer() {
		
		if (cachedReviewer != null)
			return cachedReviewer;
		
		MockPerson person = new MockPerson();
		person.firstName="Mock";
		person.lastName="Reviewer";
		person.email="test-reviewer@vireo.tdl.org";
		person.role=RoleType.REVIEWER;
		return person;
	}
	
	/**
	 * @return A new mock person with the role STUDENT.
	 */
	public static MockPerson getStudent() {
		
		if (cachedStudent != null)
			return cachedStudent;
		
		MockPerson person = new MockPerson();
		person.firstName="Mock";
		person.lastName="Student";
		person.email="test-student@vireo.tdl.org";
		person.role=RoleType.STUDENT;
		return person;
	}
	
	@Override
	public MockPerson save() {
		return this;
	}

	@Override
	public MockPerson delete() {
		return this;
	}

	@Override
	public MockPerson refresh() {
		return this;
	}

	@Override
	public MockPerson merge() {
		return this;
	}

	@Override
	public String getNetId() {
		return netid;
	}

	@Override
	public void setNetId(String netid) {
		this.netid = netid;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean validatePassword(String password) {
		if (this.password == null || password == null)
			return false;
		
		return this.password.equals(password);
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getMiddleName() {
		return middleName;
	}

	@Override
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String getFullName() {
		return firstName + " "+ lastName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public Integer getBirthYear() {
		return birthYear;
	}

	@Override
	public void setBirthYear(Integer year) {
		this.birthYear = year;
	}

	@Override
	public String getCurrentPhoneNumber() {
		return currentPhoneNumber;
	}

	@Override
	public void setCurrentPhoneNumber(String phoneNumber) {
		this.currentPhoneNumber = phoneNumber;
	}

	@Override
	public String getCurrentPostalAddress() {
		return currentPostalAddress;
	}

	@Override
	public void setCurrentPostalAddress(String postalAddress) {
		this.currentPostalAddress = postalAddress;
	}

	@Override
	public String getCurrentEmailAddress() {
		return this.currentEmailAddress;
	}

	@Override
	public void setCurrentEmailAddress(String email) {
		this.currentEmailAddress = email;
	}

	@Override
	public String getPermanentPhoneNumber() {
		return this.permanentPhoneNumber;
	}

	@Override
	public void setPermanentPhoneNumber(String phoneNumber) {
		this.permanentPhoneNumber = phoneNumber;
	}

	@Override
	public String getPermanentPostalAddress() {
		return permanentPostalAddress;
	}

	@Override
	public void setPermanentPostalAddress(String postalAddress) {
		this.permanentPostalAddress = postalAddress;
	}

	@Override
	public String getPermanentEmailAddress() {
		return permanentEmailAddress;
	}

	@Override
	public void setPermanentEmailAddress(String email) {
		this.permanentEmailAddress = email;
	}

	@Override
	public String getCurrentDepartment() {
		return currentDepartment;
	}

	@Override
	public void setCurrentDepartment(String department) {
		this.currentDepartment = department;
	}

	@Override
	public String getCurrentCollege() {
		return currentCollege;
	}

	@Override
	public void setCurrentCollege(String college) {
		this.currentCollege = college;
	}

	@Override
	public String getCurrentMajor() {
		return currentMajor;
	}

	@Override
	public void setCurrentMajor(String major) {
		this.currentMajor = major;
	}

	@Override
	public Integer getCurrentGraduationYear() {
		return currentGraduationYear;
	}

	@Override
	public void setCurrentGraduationYear(Integer year) {
		this.currentGraduationYear = year;
	}

	@Override
	public Integer getCurrentGraduationMonth() {
		return currentGraduationMonth;
	}

	@Override
	public void setCurrentGraduationMonth(Integer month) {
		this.currentGraduationMonth = month;
	}

	@Override
	public List<Preference> getPreferences() {
		return preferences;
	}

	@Override
	public Preference getPreference(String name) {
		for (Preference preference : preferences){
			if (name.equals(preference.getName()))
				return preference;
		}
		return null;
	}

	@Override
	public Preference addPreference(String name, String value) {
		MockPreference mockPref = new MockPreference();
		mockPref.person = this;
		mockPref.name = name;
		mockPref.value = value;
		preferences.add(mockPref);
		return mockPref;
	}

	@Override
	public RoleType getRole() {
		return role;
	}

	@Override
	public void setRole(RoleType role) {
		this.role = role;
	}

}
