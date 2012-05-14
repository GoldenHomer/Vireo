package org.tdl.vireo.model.jpa;

import java.util.List;
import java.util.Set;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SettingsRepository;

/**
 * Jpa specific implementation of the Vireo Person Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaPersonRepositoryImpl implements PersonRepository {

	// //////////////
	// Person Model
	// //////////////
	
	@Override
	public Person createPerson(String netid, String email, String firstName,
			String lastName, RoleType role) {
		return new JpaPersonImpl(netid, email, firstName, lastName, role);
	}

	@Override
	public Person findPerson(Long id) {
		return (Person) JpaPersonImpl.findById(id);
	}

	@Override
	public Person findPersonByEmail(String email) {
		return JpaPersonImpl.find("email = ?", email).first();
	}

	@Override
	public Person findPersonByNetId(String netid) {
		return JpaPersonImpl.find("netid = ?", netid).first();

	}

	@Override
	public List<Person> findAllPersons() {
		return (List) JpaPersonImpl.findAll();
	}

	// ///////////////////////////
	// Personal Preference Model
	// ///////////////////////////
	
	@Override
	public Preference findPreference(Long id) {
		return (Preference) JpaPreferenceImpl.findById(id);
	}

}
