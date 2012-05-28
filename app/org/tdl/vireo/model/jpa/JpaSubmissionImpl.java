package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.Model;
import play.modules.spring.Spring;

/**
 * JPA specific implementation of Vireo's Submission interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Submission")
public class JpaSubmissionImpl extends JpaAbstractModel<JpaSubmissionImpl> implements Submission {

	@ManyToOne(optional = false, targetEntity = JpaPersonImpl.class)
	public Person submitter;

	public String documentTitle;
	public String documentAbstract;
	public String documentKeywords;

	@OneToOne(targetEntity = JpaEmbargoTypeImpl.class)
	public EmbargoType embargoType;

	@OneToMany(targetEntity = JpaAttachmentImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public List<Attachment> attachments;

	@OneToMany(targetEntity = JpaCommitteeMemberImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	@OrderBy("displayOrder")
	public List<CommitteeMember> committeeMembers;
	public String committeeContactEmail;
	
	@Column(unique = true)
	public String committeeEmailHash;

	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeApprovalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeEmbargoApprovalDate;
	public String committeeDisposition;

	@Temporal(TemporalType.TIMESTAMP)
	public Date submissionDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date approvalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date licenseAgreementDate;

	public String degree;
	public String department;
	public String college;
	public String major;
	public String documentType;

	public Integer graduationYear;
	public Integer graduationMonth;

	public String stateName;

	@OneToOne(targetEntity = JpaPersonImpl.class)
	public Person assignee;
	public Boolean UMIRelease;

	@OneToMany(targetEntity = JpaCustomActionValueImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public List<CustomActionValue> customActions;
	
	@Transient
	protected List<ActionLog> pendingLogs = new ArrayList<ActionLog>();

	/**
	 * Construct a new JpaSubmissionImpl
	 * 
	 * @param submitter
	 *            The student submitting this submission.
	 */
	protected JpaSubmissionImpl(Person submitter) {

		if (submitter == null)
			throw new IllegalArgumentException("Submissions require a submitter");

		assertReviewerOrOwner(submitter);
		
		this.submitter = submitter;
		this.attachments = new ArrayList<Attachment>();
		this.committeeMembers = new ArrayList<CommitteeMember>();
		this.customActions = new ArrayList<CustomActionValue>();
		this.stateName = (Spring.getBeanOfType(StateManager.class).getInitialState()).getBeanName();
		
		generateLog("Submission created", true);
	}

	@Override
	public JpaSubmissionImpl save() {
		
		assertReviewerOrOwner(submitter);
		
		super.save();
		
		// After saving save all pending actionlogs
		for(ActionLog log : pendingLogs)
			log.save();
		pendingLogs.clear();
		
		return this;
	}
	
	@Override
	public JpaSubmissionImpl delete() {
		
		assertReviewerOrOwner(submitter);
		
		// Don't rely on the cascade for deleting attachments because the files
		// need to be deleted on disk.
		List<Attachment> attachmentsCopy = new ArrayList<Attachment>(attachments);
		for (Attachment attachment : attachmentsCopy) {
			attachment.delete();
		}
		
		// Delete all action logs associated with this submission
		em().createQuery(
			"DELETE FROM JpaActionLogImpl " +
					"WHERE Submission_Id = ? " 
			).setParameter(1, this.getId())
			.executeUpdate();

		return super.delete();
	}

	@Override
	public Person getSubmitter() {
		return submitter;
	}

	@Override
	public String getDocumentTitle() {
		return documentTitle;
	}

	@Override
	public void setDocumentTitle(String title) {
		
		assertReviewerOrOwner(submitter);
		this.documentTitle = title;
		
		generateChangeLog("Document title", title, false);
	}

	@Override
	public String getDocumentAbstract() {
		return documentAbstract;
	}

	@Override
	public void setDocumentAbstract(String docAbstract) {
		
		assertReviewerOrOwner(submitter);
		this.documentAbstract = docAbstract;
		
		generateChangeLog("Document abstract", docAbstract, false);
	}

	@Override
	public String getDocumentKeywords() {
		return documentKeywords;
	}

	@Override
	public void setDocumentKeywords(String keywords) {
		
		assertReviewerOrOwner(submitter);
		this.documentKeywords = keywords;
		
		generateChangeLog("Document keywords", keywords, false);
	}

	@Override
	public EmbargoType getEmbargoType() {
		return embargoType;
	}

	@Override
	public void setEmbargoType(EmbargoType embargo) {
		
		assertReviewerOrOwner(submitter);
		this.embargoType = embargo;
		
		generateChangeLog("Embargo type",embargo.getName(), false);
	}

	@Override
	public Attachment getPrimaryDocument() {
		for (Attachment attachment : attachments) {
			if (AttachmentType.PRIMARY == attachment.getType())
				return attachment;
		}
		
		return null;
	}

	@Override
	public List<Attachment> getSupplementalDocuments() {
		
		
		List<Attachment> supplemental = new ArrayList<Attachment>();
		for (Attachment attachment : attachments) {
			if (AttachmentType.SUPPLEMENTAL == attachment.getType())
				supplemental.add(attachment);
		}
		
		return supplemental;
	}

	@Override
	public List<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Internal call back method when an attachment has been deleted.
	 * 
	 * @param attachment
	 *            The attachment to remove.
	 */
	protected void removeAttachment(Attachment attachment) {
		
		attachments.remove(attachment);
	}

	@Override
	public Attachment addAttachment(File file, AttachmentType type)
			throws IOException {

		Attachment attachment = new JpaAttachmentImpl(this, type, file);
		attachments.add(attachment);
		return attachment;
	}

	@Override
	public List<CommitteeMember> getCommitteeMembers() {
		return committeeMembers;
	}

	@Override
	public CommitteeMember addCommitteeMember(String firstName,
			String lastName, String middleInitial, Boolean chair) {
		CommitteeMember member = new JpaCommitteeMemberImpl(this, firstName,
				lastName, middleInitial, chair);
		committeeMembers.add(member);
		return member;
	}

	/**
	 * Internal call back for when a committee member has been deleted, so that
	 * it will be removed from the list.
	 * 
	 * @param member
	 *            The member to remove.
	 */
	protected void removeCommitteeMember(CommitteeMember member) {
		this.committeeMembers.remove(member);
	}

	@Override
	public String getCommitteeContactEmail() {
		return committeeContactEmail;
	}

	@Override
	public void setCommitteeContactEmail(String email) {
		assertReviewerOrOwner(submitter);
		this.committeeContactEmail = email;
		
		generateChangeLog("Committee contact email address", email, false);
	}

	@Override
	public String getCommitteeEmailHash() {
		return committeeEmailHash;
	}

	@Override
	public void setCommitteeEmailHash(String hash) {
		assertReviewerOrOwner(submitter);
		this.committeeEmailHash = hash;
		
		generateLog("New committee email hash generated", false);
	}

	@Override
	public Date getCommitteeApprovalDate() {
		return committeeApprovalDate;
	}

	@Override
	public void setCommitteeApprovalDate(Date date) {
		assertReviewerOrOwner(submitter);
		this.committeeApprovalDate = date;
		
		if (date == null)
			generateLog("Committee approval of submission cleared",false);
		else
			generateLog("Committee approval of submission set",false);
	}

	@Override
	public Date getCommitteeEmbargoApprovalDate() {
		return committeeEmbargoApprovalDate;
	}

	@Override
	public void setCommitteeEmbargoApprovalDate(Date date) {
		assertReviewerOrOwner(submitter);
		this.committeeEmbargoApprovalDate = date;
		
		if (date == null)
			generateLog("Committee approval of embargo cleared",false);
		else
			generateLog("Committee approval of embargo set",false);
	}

	@Override
	public String getCommitteeDisposition() {
		return committeeDisposition;
	}

	@Override
	public void setCommitteeDisposition(String disposition) {
		assertReviewerOrOwner(submitter);
		this.committeeDisposition = disposition;
		
		generateChangeLog("Committee disposition",disposition,false);
	}

	@Override
	public Date getSubmissionDate() {
		return submissionDate;
	}

	@Override
	public void setSubmissionDate(Date date) {
		assertReviewerOrOwner(submitter);
		this.submissionDate = date;
		
		if (date == null)
			generateLog("Submission date cleared",true);
		else
			generateLog("Submission date set",true);
	}

	@Override
	public Date getApprovalDate() {
		return approvalDate;
	}

	@Override
	public void setApprovalDate(Date date) {
		assertReviewerOrOwner(submitter);
		this.approvalDate = date;
		
		if (date == null)
			generateLog("Submission approval cleared",true);
		else
			generateLog("Submission approval set",true);
	}

	@Override
	public Date getLicenseAgreementDate() {
		return licenseAgreementDate;
	}

	@Override
	public void setLicenseAgreementDate(Date date) {
		assertReviewerOrOwner(submitter);
		this.licenseAgreementDate = date;
		
		if (date == null)
			generateLog("Submission license agreement cleared",true);
		else
			generateLog("Submission license agreement set",true);
	}

	@Override
	public String getDegree() {
		return degree;
	}

	@Override
	public void setDegree(String degree) {
		assertReviewerOrOwner(submitter);
		this.degree = degree;
		
		generateChangeLog("Degree",degree,false);
	}

	@Override
	public String getDepartment() {
		return department;
	}

	@Override
	public void setDepartment(String department) {
		assertReviewerOrOwner(submitter);
		this.department = department;
		
		generateChangeLog("Department",department,false);
	}

	@Override
	public String getCollege() {
		return college;
	}

	@Override
	public void setCollege(String college) {
		assertReviewerOrOwner(submitter);
		this.college = college;
		
		generateChangeLog("College",college,false);
	}

	@Override
	public String getMajor() {
		return major;
	}

	@Override
	public void setMajor(String major) {
		assertReviewerOrOwner(submitter);
		this.major = major;
		
		generateChangeLog("Major",major,false);
	}

	@Override
	public String getDocumentType() {
		return documentType;
		
	}

	@Override
	public void setDocumentType(String documentType) {
		assertReviewerOrOwner(submitter);
		this.documentType = documentType;
		
		generateChangeLog("Document type",documentType,false);
	}

	@Override
	public Integer getGraduationYear() {
		return graduationYear;
	}

	@Override
	public void setGraduationYear(Integer year) {
		assertReviewerOrOwner(submitter);
		this.graduationYear = year;
		
		if (year == null)
			generateChangeLog("Graduation year", null,false);
		else
			generateChangeLog("Graduation year", String.valueOf(year),false);
	}

	@Override
	public Integer getGraduationMonth() {
		return graduationMonth;
	}

	@Override
	public void setGraduationMonth(Integer month) {
		
		if (month != null && (month < 0 || month > 11))
			throw new IllegalArgumentException("Month is out of bounds.");
		
		assertReviewerOrOwner(submitter);
		
		this.graduationMonth = month;
		
		if (month == null)
			generateChangeLog("Graduation month", null,false);
		else
			generateChangeLog("Graduation month", new DateFormatSymbols().getMonths()[month],false);
	}

	@Override
	public State getState() {
		return Spring.getBeanOfType(StateManager.class).getState(stateName);
	}

	@Override
	public void setState(State state) {
		
		if (state == null)
			throw new IllegalArgumentException("State is required");
		
		assertReviewer();
		
		this.stateName = state.getBeanName();
		
		generateChangeLog("Submission status",state.getDisplayName(),true);
	}

	@Override
	public Person getAssignee() {
		return assignee;
	}

	@Override
	public void setAssignee(Person assignee) {
		
		assertReviewer();
		
		this.assignee = assignee;
		
		if (assignee == null)
			generateChangeLog("Assignee", null,true);
		else
			generateChangeLog("Assignee",assignee.getFullName(),true);
	}

	@Override
	public Boolean getUMIRelease() {
		return UMIRelease;
	}

	@Override
	public void setUMIRelease(Boolean umiRelease) {
		
		assertReviewerOrOwner(submitter);
		this.UMIRelease = umiRelease;
		
		if (umiRelease == null)
			generateLog("UMI Release cleared",false);
		else if (umiRelease == true)
			generateChangeLog("UMI Release","Yes",false);
		else
			generateChangeLog("UMI Release","No",false);
	}

	@Override
	public List<CustomActionValue> getCustomActions() {
		return customActions;
	}
	
	@Override
	public CustomActionValue getCustomAction(CustomActionDefinition definition) {
		
		Iterator<CustomActionValue> valueItr = customActions.iterator();
		while (valueItr.hasNext()) {
			CustomActionValue value = valueItr.next();
			if (value.getDefinition().equals(definition))
				return value;
		}
		
		return null;
	}
	
	/**
	 * Internal call back to notify the parent submission when a custom action
	 * value has been deleted.
	 * 
	 * @param value
	 *            The value being deleted.
	 */
	protected void removeCustomAction(CustomActionValue value) {
		customActions.remove(value);
	}

	@Override
	public CustomActionValue addCustomAction(CustomActionDefinition definition,
			Boolean value) {
		CustomActionValue customAction = new JpaCustomActionValueImpl(this,
				definition, value);
		customActions.add(customAction);
		return customAction;
	}
	
	
	@Override
	public ActionLog logAction(String entry) {
		return logAction(entry, null);
	}
	
	/**
	 * Create an action log entry about this submission. This is method is not
	 * included in the public interface at the present time. Maybe in the future
	 * we will move it up there. This method operates exactly the same as the
	 * "logAction" method, which is defined in the public interface, but accepts
	 * an attachment object. The action log object generated will be associated
	 * with the attachment object present.
	 * 
	 * @param entry
	 *            The entry text to be saved, note " by User" will be appended
	 *            to the end of this entry text recording who made the action.
	 * @param attachment
	 *            The attachment this action log item is associated with (may be
	 *            null).
	 * @return The unsaved action log object.
	 */
	public ActionLog logAction(String entry, Attachment attachment) {
		
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		
		Person actor = context.getPerson();
		String actorName = "an unknown user";
		if (actor != null)
			actorName = actor.getFullName();
		
		entry = entry + " by " + actorName;
		
		return new JpaActionLogImpl(this, this.getState(), actor, new Date(), attachment, entry, false);
	}

	/**
	 * Internal method for generating log events.
	 * 
	 * This will create an ActionLog entry and append it to a list of pending
	 * changes. Then when this object is saved() those logs will be persisted in
	 * the database.
	 * 
	 * @param entry
	 *            The log entry (note, "by User" will be appended to the end of
	 *            the log entry)
	 * @param always
	 * 			  This log message should be generated no matter what state the submission is currently in.
	 */
	protected void generateLog(String entry, boolean always) {
		
		if (!always) {
			// Ignore if the submission is in the initial state.
			StateManager manager = Spring.getBeanOfType(StateManager.class);
			if (manager.getInitialState() == this.getState())
				return;
		}
		
		pendingLogs.add(logAction(entry));
	}
	
	/**
	 * Internal method for generating log events.
	 * 
	 * This will create a preformatted action log entry for a field change. The
	 * log will then be appended to the list of pending changes which will be
	 * persisted when the submission object is saved.
	 * 
	 * @param fieldName
	 *            The name of the field being updated.
	 * @param newValue
	 *            The new value of the field. (may be null)
	 * @param always
	 * 			  This log message should be generated no matter what state the submission is currently in.
	 */
	protected void generateChangeLog(String fieldName, String newValue, boolean always) {
		String entry;
		if (newValue == null)
			entry = fieldName + " cleared";
		else
			entry = String.format(
				"%s changed to '%s'",
				fieldName,
				newValue);
		
		generateLog(entry,always);
	}
	

}
