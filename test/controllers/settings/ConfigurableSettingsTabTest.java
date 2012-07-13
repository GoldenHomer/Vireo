package controllers.settings;
import static org.tdl.vireo.model.Configuration.ALLOW_MULTIPLE_SUBMISSIONS;
import static org.tdl.vireo.model.Configuration.CURRENT_SEMESTER;
import static org.tdl.vireo.model.Configuration.REQUEST_COLLEGE;
import static org.tdl.vireo.model.Configuration.REQUEST_UMI;
import static org.tdl.vireo.model.Configuration.SUBMISSIONS_OPEN;
import static org.tdl.vireo.model.Configuration.SUBMISSION_INSTRUCTIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the configurable settings tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ConfigurableSettingsTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	@Before
	public void setup() {
		context.turnOffAuthorization();
	}
	
	@After
	public void cleanup() {
		context.restoreAuthorization();
	}
	
	/**
	 * Just test that the page is displayed without error.
	 */
	@Test
	public void testDisplayOfConfigurableSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.ConfigurableSettingsTab.configurableSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	

	
	/**
	 * Test adding, editing, and removing a college.
	 */
	@Test
	public void testAddingEditingRemovingColleges() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addCollegeJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editCollegeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeCollegeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New College");
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findCollege(id));
		assertEquals("New College",settingRepo.findCollege(id).getName());
		
		
		// Now edit the custom action
		params.clear();
		params.put("collegeId","college_"+id);
		params.put("name", "Changed Name");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals("Changed Name",settingRepo.findCollege(id).getName());
		
		// Now remove the custom action
		params.clear();
		params.put("collegeId","college_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findCollege(id));
	}
	
	/**
	 * Test reordering a set of colleges.
	 */
	@Test
	public void testReorderingColleges() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderCollegesJSON").url;
		
		// Create two custom actions:
		College college1 = settingRepo.createCollege("test one").save();
		College college2 = settingRepo.createCollege("test two").save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("collegeIds", "college_"+college2.getId()+",college_"+college1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		college1 = settingRepo.findCollege(college1.getId());
		college2 = settingRepo.findCollege(college2.getId());
		
		assertTrue(college1.getDisplayOrder() > college2.getDisplayOrder());
		
		// Cleanup
		college1.delete();
		college2.delete();
	}
	
	
	/**
	 * Test adding, editing, and removing a department.
	 */
	@Test
	public void testAddingEditingRemovingDepartments() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addDepartmentJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editDepartmentJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeDepartmentJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Department");
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findDepartment(id));
		assertEquals("New Department",settingRepo.findDepartment(id).getName());
		
		
		// Now edit the custom action
		params.clear();
		params.put("departmentId","department_"+id);
		params.put("name", "Changed Name");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals("Changed Name",settingRepo.findDepartment(id).getName());
		
		// Now remove the custom action
		params.clear();
		params.put("departmentId","department_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findDepartment(id));
	}
	
	/**
	 * Test reordering a set of departments.
	 */
	@Test
	public void testReorderingDepartments() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderDepartmentsJSON").url;
		
		// Create two custom actions:
		Department department1 = settingRepo.createDepartment("test one").save();
		Department department2 = settingRepo.createDepartment("test two").save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("departmentIds", "department_"+department2.getId()+",department_"+department1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		department1 = settingRepo.findDepartment(department1.getId());
		department2 = settingRepo.findDepartment(department2.getId());
		
		assertTrue(department1.getDisplayOrder() > department2.getDisplayOrder());
		
		// Cleanup
		department1.delete();
		department2.delete();
	}
	
	/**
	 * Test adding, editing, and removing a major.
	 */
	@Test
	public void testAddingEditingRemovingMajors() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addMajorJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editMajorJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeMajorJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Major");
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findMajor(id));
		assertEquals("New Major",settingRepo.findMajor(id).getName());
		
		
		// Now edit the custom action
		params.clear();
		params.put("majorId","major_"+id);
		params.put("name", "Changed Name");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals("Changed Name",settingRepo.findMajor(id).getName());
		
		// Now remove the custom action
		params.clear();
		params.put("majorId","major_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findMajor(id));
	}
	
	/**
	 * Test reordering a set of majors.
	 */
	@Test
	public void testReorderingMajors() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderMajorsJSON").url;
		
		// Create two custom actions:
		Major major1 = settingRepo.createMajor("test one").save();
		Major major2 = settingRepo.createMajor("test two").save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("majorIds", "major_"+major2.getId()+",major_"+major1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		major1 = settingRepo.findMajor(major1.getId());
		major2 = settingRepo.findMajor(major2.getId());
		
		assertTrue(major1.getDisplayOrder() > major2.getDisplayOrder());
		
		// Cleanup
		major1.delete();
		major2.delete();
	}
	
	/**
	 * Test adding, editing, and removing a degree.
	 */
	@Test
	public void testAddingEditingRemovingDegrees() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addDegreeJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editDegreeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeDegreeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Degree");
		params.put("level", String.valueOf(DegreeLevel.UNDERGRADUATE.getId()));
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findDegree(id));
		assertEquals("New Degree",settingRepo.findDegree(id).getName());
		assertEquals(DegreeLevel.UNDERGRADUATE,settingRepo.findDegree(id).getLevel());

		
		// Now edit the custom action
		params.clear();
		params.put("degreeId","degree_"+id);
		params.put("name", "Changed Name");
		params.put("level", String.valueOf(DegreeLevel.DOCTORAL.getId()));
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals("Changed Name",settingRepo.findDegree(id).getName());
		assertEquals(DegreeLevel.DOCTORAL,settingRepo.findDegree(id).getLevel());

		
		// Now remove the custom action
		params.clear();
		params.put("degreeId","degree_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findDegree(id));
	}
	
	/**
	 * Test reordering a set of degrees.
	 */
	@Test
	public void testReorderingDegrees() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderDegreesJSON").url;
		
		// Create two custom actions:
		Degree degree1 = settingRepo.createDegree("test one",DegreeLevel.DOCTORAL).save();
		Degree degree2 = settingRepo.createDegree("test two",DegreeLevel.MASTERS).save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("degreeIds", "degree_"+degree2.getId()+",degree_"+degree1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		degree1 = settingRepo.findDegree(degree1.getId());
		degree2 = settingRepo.findDegree(degree2.getId());
		
		assertTrue(degree1.getDisplayOrder() > degree2.getDisplayOrder());
		
		// Cleanup
		degree1.delete();
		degree2.delete();
	}
	
	/**
	 * Test adding, editing, and removing a document type.
	 */
	@Test
	public void testAddingEditingRemovingDocumentTypes() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addDocumentTypeJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editDocumentTypeJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeDocumentTypeJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New Document Type");
		params.put("level", String.valueOf(DegreeLevel.UNDERGRADUATE.getId()));
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findDocumentType(id));
		assertEquals("New Document Type",settingRepo.findDocumentType(id).getName());
		assertEquals(DegreeLevel.UNDERGRADUATE,settingRepo.findDocumentType(id).getLevel());

		
		// Now edit the custom action
		params.clear();
		params.put("documentTypeId","documentType_"+id);
		params.put("name", "Changed Name");
		params.put("level", String.valueOf(DegreeLevel.DOCTORAL.getId()));
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals("Changed Name",settingRepo.findDocumentType(id).getName());
		assertEquals(DegreeLevel.DOCTORAL,settingRepo.findDocumentType(id).getLevel());

		
		// Now remove the custom action
		params.clear();
		params.put("documentTypeId","documentType_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findDocumentType(id));
	}
	
	/**
	 * Test reordering a set of document types.
	 */
	@Test
	public void testReorderingDocumentTypes() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderDocumentTypesJSON").url;
		
		// Create two custom actions:
		DocumentType docType1 = settingRepo.createDocumentType("test one",DegreeLevel.DOCTORAL).save();
		DocumentType docType2 = settingRepo.createDocumentType("test two",DegreeLevel.MASTERS).save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("documentTypeIds", "documentType_"+docType2.getId()+",documentType_"+docType1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		docType1 = settingRepo.findDocumentType(docType1.getId());
		docType2 = settingRepo.findDocumentType(docType2.getId());
		
		assertTrue(docType1.getDisplayOrder() > docType2.getDisplayOrder());
		
		// Cleanup
		docType1.delete();
		docType2.delete();
	}
	
	/**
	 * Test adding, editing, and removing a graduation month.
	 */
	@Test
	public void testAddingEditingRemovingGraduationMonths() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ConfigurableSettingsTab.addGraduationMonthJSON").url;
		final String EDIT_URL = Router.reverse("settings.ConfigurableSettingsTab.editGraduationMonthJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ConfigurableSettingsTab.removeGraduationMonthJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","November");
		Response response = POST(ADD_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Extract the id of the newly created action.
		Pattern ID_PATTERN = Pattern.compile("\"id\": ([0-9]+), ");
		Matcher tokenMatcher = ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String idString = tokenMatcher.group(1);
		assertNotNull(idString);
		Long id = Long.valueOf(idString);
		
		// Verify the action exists in the database.
		JPA.em().clear();
		assertNotNull(settingRepo.findGraduationMonth(id));
		assertEquals(10,settingRepo.findGraduationMonth(id).getMonth());
		
		
		// Now edit the custom action
		params.clear();
		params.put("graduationMonthId","graduationMonth_"+id);
		params.put("name", "January");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		JPA.em().clear();
		assertEquals(0,settingRepo.findGraduationMonth(id).getMonth());
		
		// Now remove the custom action
		params.clear();
		params.put("graduationMonthId","graduationMonth_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().clear();
		assertNull(settingRepo.findGraduationMonth(id));
	}
	
	/**
	 * Test reordering a set of colleges.
	 */
	@Test
	public void testReorderingGraduationMonths() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ConfigurableSettingsTab.reorderGraduationMonthsJSON").url;
		
		// Create two custom actions:
		GraduationMonth month1 = settingRepo.createGraduationMonth(6).save();
		GraduationMonth month2 = settingRepo.createGraduationMonth(10).save();
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("graduationMonthIds", "graduationMonth_"+month2.getId()+",graduationMonth_"+month1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify that the actions were reorderd
		JPA.em().clear();
		month1 = settingRepo.findGraduationMonth(month1.getId());
		month2 = settingRepo.findGraduationMonth(month2.getId());
		
		assertTrue(month1.getDisplayOrder() > month2.getDisplayOrder());
		
		// Cleanup
		month1.delete();
		month2.delete();
	}
}
