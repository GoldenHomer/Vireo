package controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.*;
import org.tdl.vireo.state.State;

import com.google.gson.Gson;

import controllers.submit.PersonalInfo;

import play.Logger;
import play.Play;
import play.libs.MimeTypes;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import sun.util.logging.resources.logging;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import play.mvc.Scope.Params;
import static org.tdl.vireo.model.Configuration.CURRENT_SEMESTER;
import static org.tdl.vireo.model.Configuration.SUBMISSIONS_OPEN;

/**
 * THIS CONTROLLER IS BEING REFACTORED. 
 * 
 * Please don't touch right now.
 */
@With(Authentication.class)
public class Student extends AbstractVireoController {

	/**
	 * Retrieve the submission object and make sure it's in a proper state. This
	 * is slightly different that the version that is in the submission steps
	 * because it does not restrict the submission to be in an initial state.
	 * 
	 * @return The submission object.
	 */
	protected static Submission getSubmission() {
		// We require an sub id.
		Long subId = params.get("subId", Long.class);
		if (subId == null) {
		    error("Did not receive the expected submission id.");
		} 
		
		// And the submission must exist.
		Submission sub = subRepo.findSubmission(subId);
		if (sub == null) {
		    error("Unable to find the submission #"+subId);
		}
		
		// Check that we are the owner of the submission.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();
		
		return sub;
	}



	/**
	 * This is a general swiss army of a controller method. This is where the
	 * student should go to start anything. If they come here and don't have
	 * anything then they will be redirected to start a new submission. However
	 * if they have other submissions then they will see a list. If the
	 * configuration is not to have multiple submissions then the student will
	 * be shutteled either to their inprogress submission, or view the status of
	 * the previous submission.
	 */
	@Security(RoleType.STUDENT)
	public static void submissionList() {	

		Person submitter = context.getPerson();
		List<Submission> submissions = subRepo.findSubmission(submitter);


		boolean submissionsOpen = (settingRepo.getConfig(Configuration.SUBMISSIONS_OPEN) != null) ? true : false;
		boolean allowMultiple = (settingRepo.getConfig(Configuration.ALLOW_MULTIPLE_SUBMISSIONS) != null) ? true : false;
		
		if (submissions.size() == 1 && !allowMultiple) {
			// If they all ready have a submission, and we don't allow multiple
			// submissions just go straight to the submission instead of the
			// list.
			Submission sub = submissions.get(0);
			
			if (sub.getState().isInProgress()) {
				// The one submission isn't complete yet.
				PersonalInfo.personalInfo(sub.getId());
			} else {
				// Go straight to view the status page.
				submissionView(sub.getId());
			}
			
		} else if(submissions.size() > 0 || !submissionsOpen) {
			// Show the list of submissions. This also handles the case if
			// submissions are closed, they will see a message telling them that.
			
			renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
			renderArgs.put("CURRENT_SEMESTER", settingRepo.getConfig(CURRENT_SEMESTER, "current"));
			
			renderTemplate("Student/list.html",submissions,allowMultiple,submissionsOpen);
		} else{
			// They don't have any submissions, and submissions are open. So
			// take them directly to the submission screen.
			PersonalInfo.personalInfo(null);
		}
	}

	/**
	 * Student view of the submission. The form in all cases allows the student
	 * to leave comments for reviewers.
	 * 
	 * However, if the submission is in a state that allows editing by students
	 * then they may modify the documents associated with the submission, before
	 * confirming their corrections.
	 * 
	 * @param subId The submission id.
	 */
	@Security(RoleType.STUDENT)
	public static void submissionView(Long subId) {		

		// Locate the submission 

		Submission sub = subRepo.findSubmission(subId);
		Person submitter = context.getPerson();

		// Handle add message button. Just add the message to the submission
		if (params.get("submit_addMessage") != null) {   
			if (!params.get("studentMessage").equals(""))
				sub.logAction("Message added : '" +	params.get("studentMessage") + "'").save();
		}
		
		if (sub.getState().isEditableByStudent()) {
			// If the replace manuscript button is pressed - then delete the manuscript 
			if (params.get("replacePrimary") != null) {
				Attachment primaryDoc = sub.getPrimaryDocument();   
				if (primaryDoc != null) {
					primaryDoc.archive();
					primaryDoc.save();
					sub.save();
				}
			}
			
			// Handle the remove supplementary document button 
			if (params.get("removeSupplementary") != null) {
				removeSupplementary(sub);           	            	
			}
			
			if(params.get("primaryDocument",File.class) != null) 
				uploadPrimaryDocument(sub);
			
			if(params.get("supplementaryDocument",File.class) != null)
				uploadSupplementary(sub);
			
			// If there is no primary document, mark it as in error.
			if (sub.getPrimaryDocument() == null)
				validation.addError("primaryDocument", "A primary document is required.");
			
			if (params.get("submit_corrections") != null) {
				try {
					context.turnOffAuthorization();
					State nextState = sub.getState().getTransitions(sub).get(0);
					sub.setState(nextState);
					sub.save();

				} finally {
					context.restoreAuthorization();

				}
			}
		}

		String grantor = settingRepo.getConfig(Configuration.GRANTOR,"Unknown Institution");
		List<Submission> allSubmissions = subRepo.findSubmission(submitter);
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Attachment primaryDocument = sub.getPrimaryDocument();
		List<Attachment> supplementaryDocuments = sub.getSupplementalDocuments();
		List<Attachment> feedbackDocuments = sub.getAttachmentsByType(AttachmentType.FEEDBACK);

		renderTemplate("Student/view.html",subId, sub, submitter, logs, primaryDocument, supplementaryDocuments, feedbackDocuments, allSubmissions, grantor);		
	}

	/**
	 * Delete a given submission
	 * 
	 * @param subId
	 *            The submission to delete.
	 */
	@Security(RoleType.STUDENT)
	public static void submissionDelete(Long subId) {
		Submission sub = getSubmission();
		sub.delete();
		
		Person submitter = context.getPerson();
		List<Submission> submissions = subRepo.findSubmission(submitter);
		if (submissions.size() == 0)
			// No other submissions, send you back to the index.
			Application.index();
		else
			// Go back to the list of other submissions.
			submissionList();
	}




	
	
	
	
	/**
	 * Helper method to handle uploading a primary document.
	 * 
	 * @param sub
	 *            The submission to add the attachment too.
	 */
	public static boolean uploadPrimaryDocument(Submission sub) {

		File primaryDocument = params.get("primaryDocument",File.class);
		if (primaryDocument == null)
			return false;

		String mimetype = MimeTypes.getContentType(primaryDocument.getName());
		if (!"application/pdf".equals(mimetype)) {
			validation.addError("primaryDocument", "Primary document must be a PDF file.");
			return false;
		}       	
		
	
		try {
			Attachment attachment = sub.addAttachment(primaryDocument, AttachmentType.PRIMARY);
			attachment.save();
			sub.save();
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to upload primary document");
			validation.addError("primaryDocument","Error uploading primary document.");
		
		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to upload primary document");
			
			if (iae.getMessage().contains("allready exists for this submission"))
				validation.addError("primaryDocument", "A file with that name allready exists; please use a different name or remove the other file.");
			else
				validation.addError("primaryDocument","Error uploading primary document.");
		
		}
		return true;
	}

	/**
	 * Helper method to handle uploading supplementary files.
	 * 
	 * @param sub
	 *            The submission to add the attachment too.
	 */
	public static boolean uploadSupplementary(Submission sub) {

		// If the upload supplementary button is pressed - then add the manuscript as an attachment
		File supplementaryDocument = params.get("supplementaryDocument",File.class);
		if (supplementaryDocument == null)
			return false;

		Attachment attachment = null;
		try {
			attachment = sub.addAttachment(supplementaryDocument, AttachmentType.SUPPLEMENTAL);                                         
			attachment.save();
			sub.save();
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to upload supplementary document");
			validation.addError("supplementaryDocument","Error uploading supplementary document.");
		
		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to upload supplementary document");
			
			if (iae.getMessage().contains("allready exists for this submission"))
				validation.addError("supplementaryDocument", "A file with that name allready exists; please use a different name or remove the other file.");
			else
				validation.addError("supplementaryDocument","Error uploading primary document.");
		
		}
		return true;
	}


	/**
	 * Helper method to handle removing supplementary files from a
	 * submission. We check that the attachments are associated with the
	 * submission, and that they are SUPPLEMENTAL files to prevent deletion of
	 * other attachments.
	 * 
	 * @param sub
	 *            The submission to remove attachments from.
	 */
	public static boolean removeSupplementary(Submission sub) {

		// Get values from all check boxes
		String[] idsToRemove = params.getAll("attachmentToRemove");

		// Iterate over all checked check boxes - removing attachments as we go
		for (String idString : idsToRemove) {
			Long id = Long.valueOf(idString);
			
			Attachment attachment = subRepo.findAttachment(id);
			
			if (attachment.getSubmission() == sub && attachment.getType() == AttachmentType.SUPPLEMENTAL)
				attachment.delete();
		}
		
		return true;
	}
	
	
	/**
	 * Helper method to view an attachment. In many places through the
	 * submission interface there are references to view attachments. All of
	 * them point here to download the file.
	 * 
	 * @param subId
	 *            The submission id.
	 * @param attachmentId
	 *            The attachment id.
	 * @param name
	 * 			  The name of the attachment (not used)
	 */
	public static void viewAttachment(Long subId, Long attachmentId, String name) {

		if (attachmentId == null)
			error();

		Submission sub = getSubmission();    	
		Attachment attachment = subRepo.findAttachment(attachmentId);

		if (attachment == null)
			error();
		if (attachment.getSubmission() != sub)
			unauthorized();

		response.setContentTypeIfNotSet(attachment.getMimeType());    	
	
		try {
			renderBinary( new FileInputStream(attachment.getFile()), attachment.getFile().length());
		} catch (FileNotFoundException ex) {
			error("File not found");
		}


	}

}