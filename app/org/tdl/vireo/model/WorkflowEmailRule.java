package org.tdl.vireo.model;

import java.util.HashMap;
import java.util.List;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.jpa.JpaEmailTemplateImpl;
import org.tdl.vireo.model.jpa.JpaEmailWorkflowRuleConditionImpl;

/**
 * This class represents the email rules which may be created in Vireo
 * 
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 */
public interface WorkflowEmailRule extends AbstractWorkflowRule {

	/**
	 * @return The email template attached to this rule
	 */
	public JpaEmailTemplateImpl getEmailTemplate();

	/**
	 * @param emailTemplate
	 *            Set the email template associated with this rule
	 */
	public void setEmailTemplate(JpaEmailTemplateImpl emailTemplate);
	
	/**
	 * @return The email group associated with this rule
	 */
	public List<String> getRecipients(Submission submission);
	
	/**
	 * 
	 * @return
	 */
	public RecipientType getRecipientType();
	
	/**
	 * 
	 * @param recipientType
	 */
	public void setRecipientType(RecipientType recipientType);

}
