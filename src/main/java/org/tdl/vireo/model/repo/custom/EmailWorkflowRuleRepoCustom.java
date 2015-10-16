package org.tdl.vireo.model.repo.custom;

import java.util.Set;

import org.tdl.vireo.enums.RecipientType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.SubmissionState;

public interface EmailWorkflowRuleRepoCustom {
	public EmailWorkflowRule create(SubmissionState submissionState, Set<Organization> organizations, RecipientType recipientType,EmailTemplate emailTemplate);
}
