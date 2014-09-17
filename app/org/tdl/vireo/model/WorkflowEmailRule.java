package org.tdl.vireo.model;

import java.util.HashMap;
import java.util.List;

/**
 * This class represents the email rules which may be created in Vireo
 * 
 * @author Jeremy Huff, huff@library.tamu.edu
 */
public interface WorkflowEmailRule extends AbstractWorkflowRule {

	/**
	 * @return The email template attached to this rule
	 */
	public EmailTemplate getEmailTemplate();

	/**
	 * @param emailTemplate
	 *            Set the email template associated with this rule
	 */
	public void setEmailTemplate(EmailTemplate emailTemplate);
	
	
	/**
	 * @param emailAddress
	 *            Set the email address associated with this rule
	 */
	public void setEmail(String emailAddress);

	/**
	 * @param emailGroup
	 *            Set the email group associated with this rule
	 */
	public void setEmail(EmailGroup emailGroup);
	
	/**
	 * @return The email group associated with this rule
	 */
	public List<String> getEmails();

}
