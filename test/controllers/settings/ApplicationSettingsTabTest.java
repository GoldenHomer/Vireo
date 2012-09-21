package controllers.settings;
import static org.tdl.vireo.model.Configuration.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

import controllers.AbstractVireoFunctionalTest;
/**
 * Test for the application settings tab. 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class ApplicationSettingsTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
	@Before
	public void setup() {
		context.turnOffAuthorization();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	@After
	public void cleanup() {
		context.restoreAuthorization();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}
	
	/**
	 * Just test that the page is displayed without error.
	 */
	@Test
	public void testDisplayOfApplicationSettingsTab() {
		
		LOGIN();
		
		final String URL = Router.reverse("settings.ApplicationSettingsTab.applicationSettings").url;

		Response response = GET(URL);
		assertIsOk(response);
	}
	
	/**
	 * Test setting and unsetting all the global configurations
	 */
	@Test
	public void testToggelingConfigurations() {
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		List<String> booleanFields = new ArrayList<String>();
		booleanFields.add(SUBMISSIONS_OPEN);
		booleanFields.add(ALLOW_MULTIPLE_SUBMISSIONS);
		booleanFields.add(SUBMIT_REQUEST_BIRTH);
		booleanFields.add(SUBMIT_REQUEST_COLLEGE);
		booleanFields.add(SUBMIT_REQUEST_UMI);
		
		
		// Get the current list of 
		List<String> originalState = new ArrayList<String>();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : booleanFields) {
			if (settingRepo.findConfigurationByName(field) != null)
				originalState.add(field);
		}
		
		
		// Set each field.
		for (String field : booleanFields) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","checked");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : booleanFields) {
			assertNotNull(settingRepo.findConfigurationByName(field));
		}
		
		// Turn off each field.
		for (String field : booleanFields) {
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("field", field);
			params.put("value","");
			Response response = POST(URL,params);
			assertContentMatch("\"success\": \"true\"", response);
		}
		
		// Check that all the fields are turned off.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		for (String field : booleanFields) {
			assertNull(settingRepo.findConfigurationByName(field));
		}
		
		// Restore to original state
		for (String field : originalState) {
			settingRepo.createConfiguration(field, "true").save();
		}
	}
	
	/**
	 * Test changing the current semester
	 */
	@Test
	public void testCurrentSemester() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
		
		// change the current semester
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", CURRENT_SEMESTER);
		params.put("value","May 2012");
		Response response = POST(URL,params);
		System.out.println(getContent(response));
		assertContentMatch("\"success\": \"true\"", response);
	
		
		// Check that all the fields are set.
		assertNotNull(settingRepo.findConfigurationByName(CURRENT_SEMESTER));
		assertEquals("May 2012",settingRepo.findConfigurationByName(CURRENT_SEMESTER).getValue());
		
		JPA.em().clear();
		if (originalValue == null) {
			settingRepo.findConfigurationByName(CURRENT_SEMESTER).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
			value.setValue(originalValue.getValue());
			value.save();
			
		}
	}
	
	/**
	 * Test changing the submission license
	 */
	@Test
	public void testSubmissionLicense() {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String URL = Router.reverse("settings.ApplicationSettingsTab.updateApplicationSettingsJSON").url;

		
		Configuration originalValue = settingRepo.findConfigurationByName(SUBMIT_LICENSE);
		
		// change the current semester
		Map<String,String> params = new HashMap<String,String>();
		params.put("field", SUBMIT_LICENSE);
		params.put("value","changed \"by test\"");
		Response response = POST(URL,params);
		assertContentMatch("\"success\": \"true\"", response);
	
		
		// Check that all the fields are set.
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNotNull(settingRepo.findConfigurationByName(SUBMIT_LICENSE));
		assertEquals("changed \"by test\"",settingRepo.findConfigurationByName(SUBMIT_LICENSE).getValue());
		
		JPA.em().clear();
		if (originalValue == null) {
			settingRepo.findConfigurationByName(SUBMIT_LICENSE).delete();
		} else {
			Configuration value = settingRepo.findConfigurationByName(SUBMIT_LICENSE);
			value.setValue(originalValue.getValue());
			value.save();
			
		}
	}
	
	
	/**
	 * Test adding and removing custom actions
	 * @throws InterruptedException 
	 */
	@Test
	public void testAddingEditingAndRemovingACustomAction() throws InterruptedException {
		
		LOGIN();
		
		// Get our urls and a list of fields.
		final String ADD_URL = Router.reverse("settings.ApplicationSettingsTab.addCustomActionJSON").url;
		final String EDIT_URL = Router.reverse("settings.ApplicationSettingsTab.editCustomActionJSON").url;
		final String REMOVE_URL = Router.reverse("settings.ApplicationSettingsTab.removeCustomActionJSON").url;

		// Add a new custom action
		Map<String,String> params = new HashMap<String,String>();
		params.put("name","New \"Custom\" action");
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
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNotNull(settingRepo.findCustomActionDefinition(id));
		
		
		// Now edit the custom action
		params.clear();
		params.put("actionId","action_"+id);
		params.put("name", "Changed Label");
		response = POST(EDIT_URL,params);
		
		// Verify the action was updated in the database.
		Thread.yield();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertEquals("Changed Label",settingRepo.findCustomActionDefinition(id).getLabel());
		
		// Now remove the custom action
		params.clear();
		params.put("actionId","action_"+id);
		response = POST(REMOVE_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		// Verify the action was deleted in the database;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		assertNull(settingRepo.findCustomActionDefinition(id));
	}
	
	/**
	 * Test reordering a set of custom actions
	 */
	@Test
	public void testReorderingCustomActions() {
		LOGIN();
		
		final String REORDER_URL = Router.reverse("settings.ApplicationSettingsTab.reorderCustomActionsJSON").url;
		
		
		// Create two custom actions:
		CustomActionDefinition action1 = settingRepo.createCustomActionDefinition("test one").save();
		CustomActionDefinition action2 = settingRepo.createCustomActionDefinition("test two").save();
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		
		// Reorder the custom actions
		Map<String,String> params = new HashMap<String,String>();
		params.put("actionIds", "action_"+action2.getId()+",action_"+action1.getId());
		Response response = POST(REORDER_URL,params);
		assertContentMatch("\"success\": \"true\"", response);
		
		
		// Verify that the actions were reorderd
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		action1 = settingRepo.findCustomActionDefinition(action1.getId());
		action2 = settingRepo.findCustomActionDefinition(action2.getId());
		
		assertTrue(action1.getDisplayOrder() > action2.getDisplayOrder());
		
		// Cleanup
		action1.delete();
		action2.delete();
	}
	
	/**
	 * Test searching for members. This tests both the blank query, pagination,
	 * and a query for "Billy"
	 */
	@Test
	public void testSearchMembers() {
		LOGIN();
		
		final String SEARCH_URL = Router.reverse("settings.ApplicationSettingsTab.searchMembers").url;
		
		// Do an empty search
		Response response = POST(SEARCH_URL);
		
		assertContentMatch("Search",response);
		List<Person> results = personRepo.searchPersons(null, 0, ApplicationSettingsTab.SEARCH_MEMBERS_RESULTS_PER_PAGE);
		for (Person result : results) {
			assertContentMatch("personId_"+result.getId(),response);
			assertContentMatch(result.getFormattedName(NameFormat.FIRST_LAST),response);
		}
		
		// Paginate to the next page
		Map<String,String> params = new HashMap<String,String>();
		params.put("query", "");
		params.put("offset", "2");
		response = POST(SEARCH_URL,params);
		
		assertContentMatch("Search",response);
		results = personRepo.searchPersons("", 2, ApplicationSettingsTab.SEARCH_MEMBERS_RESULTS_PER_PAGE);
		for (Person result : results) {
			assertContentMatch("personId_"+result.getId(),response);
			assertContentMatch(result.getFormattedName(NameFormat.FIRST_LAST),response);
		}
		
		// Do a specific search for "Billy", there should be two users from the
		// TestData loader that this matches.
		params.clear();
		params.put("query", "Billy");
		params.put("offset", "0");
		response = POST(SEARCH_URL,params);
		
		assertContentMatch("Search",response);
		results = personRepo.searchPersons("Billy", 0, ApplicationSettingsTab.SEARCH_MEMBERS_RESULTS_PER_PAGE);
		for (Person result : results) {
			assertContentMatch("personId_"+result.getId(),response);
			assertContentMatch(result.getFormattedName(NameFormat.FIRST_LAST),response);
		}
		
	}
	
	/**
	 * Test updated a person's role. 
	 */
	@Test
	public void testUpdatePersonRole() {
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("settings.ApplicationSettingsTab.updatePersonRole").url;
		
		// Create a person
		Person person = personRepo.createPerson("netid", "email@email.com", "firstName", "lastName", RoleType.NONE).save();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		
		// Upgrade person to a reviewer
		Map<String,String> params = new HashMap<String,String>();
		params.put("personId", "personId_"+person.getId());
		params.put("role", String.valueOf(RoleType.REVIEWER.getId()));
		Response response = POST(UPDATE_URL,params);
		
		assertContentMatch("Add Member",response);
		assertContentMatch("personId_"+person.getId(),response);
		assertContentMatch(person.getFormattedName(NameFormat.FIRST_LAST),response);
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		personRepo.findPerson(person.getId()).delete();
		
		
	}
	
	
}
