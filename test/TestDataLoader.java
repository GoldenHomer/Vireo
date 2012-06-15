import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.security.impl.ShibbolethAuthenticationMethodImpl;

import play.Logger;
import play.db.jpa.JPA;
import play.jobs.*;
import play.modules.spring.Spring;

/**
 * When running in a test environment pre-load some configuration and test data
 * just to make things easier.
 * 
 * 
 * @author Micah Cooper
 * @author <a herf="www.scottphillips.com">Scott Phillips</a>
 * 
 */
@OnApplicationStart
public class TestDataLoader extends Job {

	/**
	 * Initial Persons to create
	 */
	private static final PersonsArray[] PERSONS_DEFINITIONS = {
		new PersonsArray("000000001", "bthornton@gmail.com", "Billy", "Thornton", "password",  RoleType.ADMINISTRATOR),
		new PersonsArray("000000002", "mdriver@gmail.com", "Minnie", "Driver", "password", RoleType.MANAGER),
		new PersonsArray("000000003", "jdimaggio@gmail.com", "John", "Di Maggio", "password", RoleType.REVIEWER),
		new PersonsArray("000000004", "cdanes@gmail.com", "Claire", "Danes", "password", RoleType.STUDENT),
		new PersonsArray("000000005", "bcrudup@gmail.com", "Billy", "Crudup", "password", RoleType.STUDENT),
		new PersonsArray("000000006", "ganderson@gmail.com", "Gillian", "Anderson", "password", RoleType.STUDENT)
	};
	
	/**
	 * Initial Colleges to create
	 */
	private static final String[] COLLEGES_DEFINITIONS = {
		"College of Agriculture and Life Sciences",
		"College of Architecture",
		"College of Education and Human Development",
		"College of Geosciences",
		"College of Liberal Arts",
		"College of Science",
		"College of Veterinary Medicine and Biomedical Sciences",
		"Dwight Look College of Engineering",
		"Interdisciplinary Degree Programs",
		"Mays Business School",
		"Texas A&M University at Galveston",
		"Texas A&M University at Qatar",
		"Undergraduate Honors Fellows",
		"Undergraduate Scholars"
	};
	
	/**
	 * Initial Departments to create
	 */
	
	private static final String[] DEPARTMENTS_DEFINITIONS = {
		"Accounting",
		"Aerospace Engineering",
		"Agricultural Economics",
		"Agricultural Leadership, Education, and Communications",
		"Animal Science",
		"Anthropology",
		"Architecture",
		"Atmospheric Sciences",
		"Biochemistry and Biophysics",
		"Biological and Agricultural Engineering",
		"Biology",
		"Biomedical Engineering",
		"Chemical Engineering",
		"Chemistry",
		"Civil Engineering",
		"College of Agriculture and Life Sciences",
		"College of Architecture",
		"College of Education and Human Development",
		"College of Engineering",
		"College of Geosciences",
		"College of Liberal Arts",
		"College of Science",
		"College of Veterinary Medicine and Biomedical Sciences",
		"Communication",
		"Computer Science and Engineering",
		"Construction Science",
		"Economics",
		"Ecosystem Science and Management",
		"Educational Administration and Human Resource Development",
		"Educational Psychology",
		"Electrical and Computer Engineering",
		"English",
		"Entomology",
		"Finance",
		"Geography",
		"Geology and Geophysics",
		"Health and Kinesiology",
		"Hispanic Studies",
		"History",
		"Horticultural Sciences",
		"Industrial and Systems Engineering",
		"Information and Operations Management",
		"Landscape Architecture and Urban Planning",
		"Management",
		"Marine Biology",
		"Marine Sciences",
		"Marketing",
		"Mathematics",
		"Mays Business School",
		"Mechanical Engineering",
		"Nuclear Engineering",
		"Nutrition and Food Science",
		"Oceanography",
		"Performance Studies",
		"Petroleum Engineering",
		"Philosophy and Humanities",
		"Physics and Astronomy",
		"Plant Pathology and Microbiology",
		"Political Science",
		"Poultry Science",
		"Psychology",
		"Recreation, Park, and Tourism Sciences",
		"Sociology",
		"Soil and Crop Sciences",
		"Statistics",
		"Teaching, Learning, and Culture",
		"Veterinary Integrative Biosciences",
		"Veterinary Large Animal Clinical Sciences",
		"Veterinary Pathobiology",
		"Veterinary Physiology and Pharmacology",
		"Veterinary Small Animal Clinical Sciences",
		"Visualization",
		"Wildlife and Fisheries Sciences"
	};
	
	/**
	 * Initial Majors to create
	 */
	
	private static final String[] MAJORS_DEFINITIONS = {
		"Accounting",
		"Aerospace Engineering",
		"Agribusiness",
		"Agribusiness and Managerial Economics",
		"Agricultural Economics",
		"Agricultural Leadership, Education, and Communications",
		"Agricultural Systems Management",
		"Agronomy",
		"Animal Breeding",
		"Animal Science",
		"Anthropology",
		"Applied Physics",
		"Architecture",
		"Atmospheric Sciences",
		"Bilingual Education",
		"Biochemistry",
		"Biological and Agricultural Engineering",
		"Biology",
		"Biomedical Engineering",
		"Biomedical Sciences",
		"Biotechnology",
		"Botany",
		"Business Administration",
		"Chemical Engineering",
		"Chemistry",
		"Civil Engineering",
		"Communication",
		"Comparative Literature and Culture",
		"Computer Engineering",
		"Computer Science",
		"Construction Management",
		"Counseling Psychology",
		"Curriculum and Instruction",
		"Dairy Science",
		"Economics",
		"Educational Administration",
		"Educational Human Resource Development",
		"Educational Psychology",
		"Electrical Engineering",
		"Engineering",
		"Engineering Systems Management",
		"English",
		"Entomology",
		"Epidemiology",
		"Finance",
		"Floriculture",
		"Food Science and Technology",
		"Forestry",
		"Genetics",
		"Geography",
		"Geology",
		"Geophysics",
		"Health Education",
		"Health Physics",
		"Hispanic Studies",
		"History",
		"Horticulture",
		"Industrial Engineering",
		"Interdisciplinary Engineering",
		"Kinesiology",
		"Laboratory Animal Medicine",
		"Management",
		"Management Information Systems",
		"Marine Biology",
		"Marine Resources Management",
		"Marketing",
		"Materials Science and Engineering",
		"Mathematics",
		"Mechanical Engineering",
		"Microbiology",
		"Modern Languages",
		"Molecular and Environmental Plant Sciences",
		"Nuclear Engineering",
		"Nutrition",
		"Ocean Engineering",
		"Oceanography",
		"Performance Studies",
		"Petroleum Engineering",
		"Philosophy",
		"Physical Education",
		"Physics",
		"Physiology of Reproduction",
		"Plant Breeding",
		"Plant Pathology",
		"Political Science",
		"Poultry Science",
		"Psychology",
		"Rangeland Ecology and Management",
		"Recreation, Park, and Tourism Sciences",
		"Safety Engineering",
		"School Psychology",
		"Science and Technology Journalism",
		"Sociology",
		"Soil Science",
		"Sport Management",
		"Statistics",
		"Toxicology",
		"University Studies",
		"Urban and Regional Planning",
		"Urban and Regional Sciences",
		"Veterinary Anatomy",
		"Veterinary Microbiology",
		"Veterinary Pathology",
		"Veterinary Medical Sciences",
		"Veterinary Parasitology",
		"Veterinary Physiology",
		"Veterinary Public Health",
		"Visualization Sciences",
		"Water Management and Hydrological Science",
		"Wildlife and Fisheries Sciences",
		"Wildlife Science",
		"Zoology"
	};
	
	/**
	 * Initial Degrees to create
	 */

	private static final DegreeLevelArray[] DEGREES_DEFINITIONS = {
		new DegreeLevelArray("Doctor of Philosophy", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Engineering", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Education", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Doctor of Musical Arts", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Master of Arts", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Landscape Architecture", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Marine Resources Management", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Public Affairs", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Science", DegreeLevel.MASTERS),
		new DegreeLevelArray("Master of Urban Planning", DegreeLevel.MASTERS),
		new DegreeLevelArray("Bachelor of Arts", DegreeLevel.UNDERGRADUATE),
		new DegreeLevelArray("Bachelor of Science", DegreeLevel.UNDERGRADUATE),
		new DegreeLevelArray("Bachelor of Environmental Design", DegreeLevel.UNDERGRADUATE)
	};
	
	/**
	 * Initial Document Types to create
	 */
	
	private static final DegreeLevelArray[] DOCTYPES_DEFINITIONS = {
		new DegreeLevelArray("Record of Study", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Dissertation", DegreeLevel.DOCTORAL),
		new DegreeLevelArray("Thesis", DegreeLevel.MASTERS),
		new DegreeLevelArray("Thesis", DegreeLevel.UNDERGRADUATE)
	};
	
	/**
	 * Initial Graduation Months to create
	 */
	
	private static final int[] GRAD_MONTHS_DEFINITIONS = {
		4, 7, 11 // 0 = january, 11 = december
	};
	
	/**
	 * Initial Embargo Types to create
	 */
	
	private static final EmbargoArray[] EMBARGO_DEFINTITIONS = {
		new EmbargoArray("None", "The work will be published after approval.", 0, true),
		new EmbargoArray("Journal Hold",
				"The full text of this work will be held/restricted from worldwide access on the internet for one year from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication. For doctoral students, the abstract of the work will be available through ProQuest/UMI during this time.", 
				12,
				true),
		new EmbargoArray("Patent Hold",
				"The full text of this work will be held/restricted from public access temporarily because of patent related activities or for proprietary purposes. The faculty chair will be contacted on an annual basis, and the work will be released following the chair's approval.",
				24,
				true
				),
	    new EmbargoArray("Other Embargo Period",
	    		"The work will be delayed for publication by an indefinite amount of time.",
	    		null,
	    		false),
	    new EmbargoArray("2-year Journal Hold",
	    		"The full text of this work will be held/restricted from worldwide access on the internet for two years from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication. The abstract of the work will be available through Texas A&M Libraries and, for doctoral students, through ProQuest/UMI during this time.",
	    		null,
	    		true)
	};
		
	/**
	 * Generate Persons, Colleges, Departments, Majors,
	 * Degrees, Document Types and Graduation Months. 
	 */
	
	@Override
	public void doJob() {
		try {
			
		
			SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
			PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
			SettingsRepository settingsRepo = Spring.getBeanOfType(SettingsRepository.class);
			ShibbolethAuthenticationMethodImpl shib = Spring.getBeanOfType(ShibbolethAuthenticationMethodImpl.class);
			
			// Turn off authorizations.
			context.turnOffAuthorization(); 
			try {
				
				// Create all persons
				for(PersonsArray personDefinition : PERSONS_DEFINITIONS) {
					Person person = personRepo.createPerson(personDefinition.netId, personDefinition.email, personDefinition.firstName, personDefinition.lastName, personDefinition.role);
					person.setPassword(personDefinition.password);
					person.save();
				}
				// Special case. Initialize Billy-bob with all the data defined by the shibboleth authentication. This results in a lot less confusion when the authentitation changes a person's metadat.
				
				boolean originalMock = shib.mock;
				shib.mock = true;
				shib.authenticate(null);
				shib.mock = originalMock;
				
				// Create all colleges
				for(String collegeDefinition : COLLEGES_DEFINITIONS) {
					settingsRepo.createCollege(collegeDefinition).save();
				}
				
				// Create all departments
				for(String departmentDefinition : DEPARTMENTS_DEFINITIONS) {
					settingsRepo.createDepartment(departmentDefinition).save();
				}
				
				// Create all majors
				for(String majorDefinition : MAJORS_DEFINITIONS) {
					settingsRepo.createMajor(majorDefinition).save();
				}
				
				// Create all degrees
				for(DegreeLevelArray degreeDefinition : DEGREES_DEFINITIONS) {
					settingsRepo.createDegree(degreeDefinition.name, degreeDefinition.degreeLevel).save();
				}
				
				// Create all document types
				for(DegreeLevelArray docTypeDefinition : DOCTYPES_DEFINITIONS) {
					settingsRepo.createDocumentType(docTypeDefinition.name, docTypeDefinition.degreeLevel).save();
				}
				
				// Create all graduation months
				for(int gradMonthDefinition : GRAD_MONTHS_DEFINITIONS) {
					settingsRepo.createGraduationMonth(gradMonthDefinition).save();
				}
				
				// Create all embargo types
				for(EmbargoArray embargoDefinition : EMBARGO_DEFINTITIONS) {
					settingsRepo.createEmbargoType(embargoDefinition.name, embargoDefinition.description, embargoDefinition.duration, embargoDefinition.active).save();
				}
				
				// Save the database state
				JPA.em().flush();
			} finally {
				context.restoreAuthorization();
			}
			
		} catch (Exception e) {Logger.error(e, "Unable to load test data.");}
	}
	
	private static class DegreeLevelArray {
		String name;
		DegreeLevel degreeLevel;
		
		DegreeLevelArray(String name, DegreeLevel degreeLevel) {
			this.name = name;
			this.degreeLevel = degreeLevel;
		}
	}
	
	private static class PersonsArray {
		String netId;
		String email;
		String firstName;
		String lastName;
		String password;
		RoleType role;
		
		PersonsArray(String netId, String email, String firstName, String lastName, String password, RoleType role) {
			this.netId = netId;
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
			this.password = password;
			this.role = role;
		}
	}
	
	private static class EmbargoArray{
		
		String name;
		String description;
		Integer duration;
		boolean active;
		
		EmbargoArray(String name, String description, Integer duration, boolean active) {
			this.name = name;
			this.description = description;
			this.duration = duration;
			this.active = active;
		}
		
		
	}
}

