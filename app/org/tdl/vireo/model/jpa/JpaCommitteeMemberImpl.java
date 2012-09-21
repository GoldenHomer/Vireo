package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.StateManager;

import play.modules.spring.Spring;

/**
 * Jpa specific implementation of Vireo's CommitteeMember interface
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "committee_member")
public class JpaCommitteeMemberImpl extends JpaAbstractModel<JpaCommitteeMemberImpl> implements CommitteeMember {

	@Column(nullable = false)
	public int displayOrder;

	@ManyToOne(targetEntity=JpaSubmissionImpl.class, optional=false)
	public Submission submission;

	@Column(length=255)
	public String firstName;
	
	@Column(length=255)
	public String lastName;
	
	@Column(length=255)
	public String middleName;
	public boolean chair;

	/**
	 * Create a new JpaCommitteeMemberImpl
	 * 
	 * @param submission
	 *            The submission this member belongs too.
	 * @param firstName
	 *            The first name of the member.
	 * @param lastName
	 *            The last name of the member.
	 * @param middleName
	 *            The middle name of the member.
	 * @param chair
	 *            Weather this member is a chair or co-chair.
	 */
	protected JpaCommitteeMemberImpl(Submission submission, String firstName,
			String lastName, String middleName, boolean chair) {

		if (submission == null)
			throw new IllegalArgumentException("Submissions are required");
		
		if (firstName != null && firstName.trim().length() == 0)
			firstName = null;
		
		if (lastName != null && lastName.trim().length() == 0)
			lastName = null;
		
		if (firstName == null && lastName == null)
			throw new IllegalArgumentException("Either a first or a last name is required.");
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		this.submission = submission;
	    this.displayOrder = 0;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleName = middleName;
		this.chair = chair;
	}

	@Override
	public JpaCommitteeMemberImpl save() {
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		// Do the check to ensure that there it at least a first or a last name available.
		if ((firstName == null || firstName.length() == 0) &&
			(lastName == null || lastName.length() == 0))
			throw new IllegalArgumentException("Either a first or a last name is required.");
		
		boolean newObject = false;
		if (id == null)
			newObject = true;
		
		super.save();
		
		// Ignore the log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {
				if (newObject) {
					
				// We're a new object so log the addition.
				String entry = "Committee member '"+this.getFormattedName(NameFormat.FIRST_MIDDLE_LAST)+"'"+(this.isCommitteeChair() ? " as chair" : "")+" added";
				submission.logAction(entry).save();
	
			} else {
				
				// We've been updated so log the change.
				String entry = "Committee member '"+this.getFormattedName(NameFormat.FIRST_MIDDLE_LAST)+"'"+(this.isCommitteeChair() ? " as chair" : "")+" modified";
				submission.logAction(entry).save();
			}
		}

		
		return this;
	}
	
	@Override
	public JpaCommitteeMemberImpl delete() {
		
		assertReviewerOrOwner(submission.getSubmitter());
		
		((JpaSubmissionImpl) submission).removeCommitteeMember(this);

		super.delete();
		
		// Ignore the log message if the submission is in the initial state.
		StateManager manager = Spring.getBeanOfType(StateManager.class);
		if (manager.getInitialState() != submission.getState()) {
			
			String entry = "Committee member '"+this.getFormattedName(NameFormat.FIRST_MIDDLE_LAST)+"'"+(this.isCommitteeChair() ? " as chair" : "")+" removed";
			submission.logAction(entry).save();
		}
		
		return this;
	}
	
    @Override
    public int getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(int displayOrder) {
    	
		assertReviewerOrOwner(submission.getSubmitter());
        this.displayOrder = displayOrder;
    }

	@Override
	public Submission getSubmission() {
		return this.submission;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void setLastName(String lastName) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.lastName = lastName;
	}

	@Override
	public String getMiddleName() {
		return this.middleName;
	}

	@Override
	public void setMiddleName(String middleName) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.middleName = middleName;
	}
	
	@Override
	public String getFormattedName(NameFormat format) {
		
		return NameFormat.format(format, firstName, middleName, lastName, null);
	}

	@Override
	public boolean isCommitteeChair() {
		return this.chair;
	}

	@Override
	public void setCommitteeChair(boolean chair) {
		
		assertReviewerOrOwner(submission.getSubmitter());
		this.chair = chair;
	}

}
