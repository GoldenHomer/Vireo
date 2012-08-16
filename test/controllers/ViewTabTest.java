package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.state.impl.StateManagerImpl;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Scope.Session;

/**
 * Test the methods of the view tab.
 * 
 * @author Micah Cooper
 *
 */
public class ViewTabTest extends AbstractVireoFunctionalTest {

	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);
	
	/**
	 * Test that an admin can change an item on a submission.
	 */
	@Test
	public void testUpdateJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		submission.setDocumentTitle("My Document Title");
		submission.setDocumentAbstract("My Document Abstract");
		submission.save();
		Long id = submission.getId();
		
		assertEquals("My Document Title", submission.getDocumentTitle());
		assertEquals("My Document Abstract", submission.getDocumentAbstract());
		
		JPA.em().detach(submission);
		
		LOGIN();
		
		final String UPDATE_URL = Router.reverse("ViewTab.updateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("field", "title");
		params.put("value", "This is a new title");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		assertFalse("My Document Title".equals(submission.getDocumentTitle()));
		assertEquals("This is a new title", submission.getDocumentTitle());
		assertEquals("My Document Abstract", submission.getDocumentAbstract());
		
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that an admin can add a committee member.
	 */
	@Test
	public void testAddCommitteeMemberJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.save();
		Long id = submission.getId();
		
		assertEquals(0, submission.getCommitteeMembers().size());
		
		JPA.em().detach(submission);
		
		LOGIN();	
		
		final String UPDATE_URL = Router.reverse("ViewTab.addCommitteeMemberJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("firstName", "John");
		params.put("lastName", "Doe");
		params.put("middleName", "T");
		params.put("chair", "true");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		CommitteeMember member = submission.getCommitteeMembers().get(0);
		
		assertEquals("John", member.getFirstName());
		assertEquals("Doe", member.getLastName());
		assertEquals("T", member.getMiddleName());
		assertTrue(member.isCommitteeChair());
		
		member.delete();
		submission.delete();		
		
		context.restoreAuthorization();
		
	}
	
	/**
	 * Test that an admin can update a committee member
	 */
	@Test
	public void testUpdateCommitteeMemberJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.addCommitteeMember("John", "Doe", "T", true);
		submission.save();
		
		CommitteeMember member = submission.getCommitteeMembers().get(0);
		Long subId = submission.getId();
		Long id = member.getId();
		
		assertEquals(1, submission.getCommitteeMembers().size());
		assertEquals("John", member.getFirstName());
		assertEquals("Doe", member.getLastName());
		assertEquals("T", member.getMiddleName());
		assertTrue(member.isCommitteeChair());
		
		JPA.em().detach(submission);
		JPA.em().detach(member);
		
		LOGIN();	
		
		final String UPDATE_URL = Router.reverse("ViewTab.updateCommitteeMemberJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("firstName", "Jill");
		params.put("lastName", "Duck");
		params.put("middleName", "M");
		params.put("chair", "false");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(subId);		
		member = subRepo.findCommitteeMember(id);
		
		assertEquals("Jill", member.getFirstName());
		assertEquals("Duck", member.getLastName());
		assertEquals("M", member.getMiddleName());
		assertFalse(member.isCommitteeChair());
		
		member.delete();
		submission.delete();		
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that an admin can delete a committee member
	 */
	@Test
	public void testRemoveCommitteeMemberJSON() {
		context.turnOffAuthorization();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.addCommitteeMember("John", "Doe", "T", true);
		submission.save();
		
		CommitteeMember member = submission.getCommitteeMembers().get(0);
		Long subId = submission.getId();
		Long id = member.getId();
		
		assertEquals(1, submission.getCommitteeMembers().size());
		assertEquals("John", member.getFirstName());
		assertEquals("Doe", member.getLastName());
		assertEquals("T", member.getMiddleName());
		assertTrue(member.isCommitteeChair());
		
		JPA.em().detach(submission);
		JPA.em().detach(member);
		
		LOGIN();	
		
		final String UPDATE_URL = Router.reverse("ViewTab.removeCommitteeMemberJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(subId);		
		member = subRepo.findCommitteeMember(id);
		
		assertNull(member);
		
		submission.delete();		
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that the action log table gets refreshed properly.
	 */
	@Test
	public void testRefreshActionLogTable() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		submission.setDocumentTitle("My Document Title");
		State state = stateManager.getState("InReview");
		submission.setState(state);
		submission.save();
		Long id = submission.getId();
		
		assertEquals("My Document Title", submission.getDocumentTitle());
				
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.updateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("field", "title");
		params.put("value", "This is a new title");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		UPDATE_URL = Router.reverse("ViewTab.refreshActionLogTable").url;
		
		params.clear();
		params.put("id", id.toString());
		
		response = POST(UPDATE_URL,params);
		assertIsOk(response);
		//java.lang.System.out.println(getContent(response));
		assertContentMatch("Document title changed to",response);				
		
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that the left column gets refreshed properly.
	 */
	@Test
	public void testRefreshLeftColumn() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		submission.setDocumentTitle("My Document Title");
		State state = stateManager.getState("InReview");
		submission.setState(state);
		EmbargoType embargo = settingRepo.findAllEmbargoTypes().get(0);
		submission.setEmbargoType(embargo);
		submission.save();
		Long id = submission.getId();
		
		assertEquals("My Document Title", submission.getDocumentTitle());
				
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.updateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("field", "title");
		params.put("value", "This is a new title");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		submission = subRepo.findSubmission(id);
		
		UPDATE_URL = Router.reverse("ViewTab.refreshLeftColumn").url;
		
		params.clear();
		params.put("id", id.toString());
		
		response = POST(UPDATE_URL,params);
		assertIsOk(response);
		//java.lang.System.out.println(getContent(response));
		assertContentMatch("Document title changed to",response);				
		
		submission.delete();
		
		context.restoreAuthorization();		
	}
	
	/**
	 * Test that an admin can change the submission status
	 */
	@Test
	public void testChangeSubmissionStatus() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");		
		Submission submission = subRepo.createSubmission(person);
		State state = stateManager.getState("InReview");
		submission.setState(state);
		submission.save();
		Long id = submission.getId();
		
		assertEquals(submission.getState().getBeanName(), "InReview");
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.changeSubmissionStatus").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("submission-status", "NeedsCorrection");
		params.put("depositLocationId", "1");
		params.put("special_value", "");
		
		Response response = POST(UPDATE_URL,params);
		assertStatus(302, response);
		
		submission = subRepo.findSubmission(id);	
		
		assertEquals(submission.getState().getBeanName(), "NeedsCorrection");
		
		submission.delete();
		
		context.restoreAuthorization();	
	}
	
	/**
	 * Test that an admin can change the submission assignee
	 */
	@Test
	public void testChangeAssignedTo() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Person newPerson = personRepo.createPerson("jdoe", "jdoe@gmail.com", "John", "Doe", RoleType.REVIEWER);
		newPerson.save();
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		Long personId = newPerson.getId();
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "bthornton@gmail.com");
		
		JPA.em().detach(submission);
		JPA.em().detach(newPerson);
		
		String UPDATE_URL = Router.reverse("ViewTab.changeAssignedTo").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("assignee", personId.toString());
		params.put("special_value", "");
		
		Response response = POST(UPDATE_URL,params);
		assertStatus(302, response);
		
		submission = subRepo.findSubmission(id);	
		newPerson = personRepo.findPerson(personId);
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "jdoe@gmail.com");
		
		submission.delete();
		newPerson.delete();
		
		context.restoreAuthorization();	
	}
	
	/**
	 * Test that an admin can add an action log comment
	 */
	@Test
	public void testAddActionLogComment() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		int numActionLogs = subRepo.findActionLog(submission).size();
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "bthornton@gmail.com");
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addActionLogComment").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("subject", "The subject");
		params.put("comment", "This is the comment.");
		params.put("visibility", "public");
		
		Response response = POST(UPDATE_URL,params);
		assertStatus(302, response);
		
		submission = subRepo.findSubmission(id);
		assertTrue(subRepo.findActionLog(submission).size()>numActionLogs);
		assertEquals("This is the comment. by Billy Thornton", subRepo.findActionLog(submission).get(0).getEntry());
		
		submission.delete();
		
		context.restoreAuthorization();		
	}
	
	/**
	 * Test that an email template returns successful with a subject and message
	 */
	@Test
	public void testRetrieveTemplateJSON() {
		context.turnOffAuthorization();
		LOGIN();
		
		EmailTemplate template = settingRepo.createEmailTemplate("newTemplate", "New Template Subject", "New Template Message");
		template.save();
		Long id = template.getId();
		
		JPA.em().detach(template);
		
		String UPDATE_URL = Router.reverse("ViewTab.retrieveTemplateJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		assertContentMatch("\"success\": true,",response);
		
		template = settingRepo.findEmailTemplate(id);
		template.delete();
		
		context.restoreAuthorization();		
	}
	
	/**
	 * Test that an admin can change the custom action values.
	 */
	@Test
	public void testUpdateCustomActionsJSON() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		assertEquals(submission.getAssignee().getCurrentEmailAddress(), "bthornton@gmail.com");
		
		CustomActionDefinition actionDef = settingRepo.createCustomActionDefinition("Passed Classes").save();
		
		Long actionId = actionDef.getId();
		
		JPA.em().detach(submission);
		JPA.em().detach(actionDef);
		
		String UPDATE_URL = Router.reverse("ViewTab.updateCustomActionsJSON").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("id", id.toString());
		params.put("action", actionId.toString());
		params.put("value", "true");
		
		Response response = POST(UPDATE_URL,params);
		assertIsOk(response);
		
		submission = subRepo.findSubmission(id);
		actionDef = settingRepo.findCustomActionDefinition(actionId);
		
		assertTrue(submission.getCustomAction(actionDef).getValue());
		
		submission.delete();
		actionDef.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test that an admin can add a file.
	 */
	@Test
	public void testAddFile() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addFile").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "note");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SampleSupplementalDocument.doc");
			files.put("noteAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertStatus(302,response);
		
		file.delete();
		submission = subRepo.findSubmission(id);
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test uploading a "note" file
	 */
	@Test
	public void testUploadNote() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addFile").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "note");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SampleFeedbackDocument.png");
			files.put("noteAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertStatus(302,response);
		
		file.delete();
		submission = subRepo.findSubmission(id);
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test uploading a "supplement" file
	 */
	@Test
	public void testUploadSupplement() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addFile").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "supplement");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SampleSupplementalDocument.doc");
			files.put("supplementAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertStatus(302,response);
		
		file.delete();
		submission = subRepo.findSubmission(id);
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test removing a "supplement" file
	 */
	@Test
	public void testRemoveSupplement() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addFile").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "supplement");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SampleSupplementalDocument.doc");
			files.put("supplementAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertStatus(302,response);

		submission = subRepo.findSubmission(id);
		
		Long fileId = submission.getSupplementalDocuments().get(0).getId();
		
		JPA.em().detach(submission);
		
		params.put("supplementType", "delete");
		params.put("supplementDelete", fileId.toString());
		
		response = POST(UPDATE_URL,params);
		assertStatus(302,response);
		
		submission = subRepo.findSubmission(id);
		
		assertEquals(0,submission.getSupplementalDocuments().size());
		
		file.delete();
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test uploading a "primary" file
	 */
	@Test
	public void testUploadPrimary() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addFile").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "primary");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SamplePrimaryDocument.pdf");
			files.put("primaryAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertStatus(302,response);
		
		file.delete();
		submission = subRepo.findSubmission(id);
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test the viewFile page
	 */
	@Test
	public void testViewFile() {
		context.turnOffAuthorization();
		LOGIN();
		
		Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
		Submission submission = subRepo.createSubmission(person);
		submission.setAssignee(person);
		submission.save();
		
		Long id = submission.getId();
		
		JPA.em().detach(submission);
		
		String UPDATE_URL = Router.reverse("ViewTab.addFile").url;
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("subId", id.toString());
		params.put("uploadType", "note");
		
		Map<String,File> files = new HashMap<String,File>();
		File file = null;
		
		try {
			file = getResourceFile("SamplePrimaryDocument.pdf");
			files.put("noteAttachment", file);
		} catch (IOException ioe) {
			fail("Test upload file not found.");
		}
		
		Response response = POST(UPDATE_URL,params,files);
		assertStatus(302,response);
		
		submission = subRepo.findSubmission(id);
		Long fileId = submission.getAttachments().get(0).getId();
		String fileName = submission.getAttachments().get(0).getName();
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("id", fileId.toString());
		routeArgs.put("name", fileName);
		
		UPDATE_URL = Router.reverse("ViewTab.viewFile",routeArgs).url;		
		
		Session.current().put("submission", id.toString());
					
		response = GET(UPDATE_URL);
		
		assertStatus(302, response);
		
		Session.current().clear();
		file.delete();
		submission.delete();
		
		context.restoreAuthorization();
	}
	
	/**
	 * Test parsing a graduation date
	 */
	@Test
	public void testParseGraduation() {
		List<String> parsedGrad = ViewTab.parseGraduation("May 2012");
		
		assertEquals("May",parsedGrad.get(0));
		assertEquals("2012",parsedGrad.get(1));
	}
	
	/**
	 * Test converting a month String to an integer
	 */
	@Test
	public void testMonthNameToInt() {
		int month = ViewTab.monthNameToInt("May");
		
		assertEquals(4,month);
	}
	
	/**
     * Extract the file from the jar and place it in a temporary location for the test to operate from.
     *
     * @param filePath The path, relative to the classpath, of the file to reference.
     * @return A Java File object reference.
     * @throws IOException
     */
    protected static File getResourceFile(String filePath) throws IOException {

        File file = File.createTempFile("ingest-import-test", ".pdf");

        // While we're packaged by play we have to ask Play for the inputstream instead of the classloader.
        //InputStream is = DSpaceCSVIngestServiceImplTests.class
        //		.getResourceAsStream(filePath);
        InputStream is = Play.classloader.getResourceAsStream(filePath);
        OutputStream os = new FileOutputStream(file);

        // Copy the file out of the jar into a temporary space.
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        is.close();
        os.close();

        return file;
    }
}
