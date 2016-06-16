package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.SubmissionWorkflowStepRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.SubmissionRepoCustom;

import edu.tamu.framework.model.Credentials;

public class SubmissionRepoImpl implements SubmissionRepoCustom {

    @Autowired
    private SubmissionRepo submissionRepo;
    
    @Autowired
    private SubmissionStateRepo submissionStateRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private SubmissionWorkflowStepRepo submissionWorkflowStepRepo;

    @Override
    public Submission create(Credentials submitterCredentials, Long organizationId) {
        
        User submitter = userRepo.findByEmail(submitterCredentials.getEmail());
        // TODO: Instead of being hardcoded this could be dynamic either at the app level, or per organization
        SubmissionState startingState = submissionStateRepo.findByName("In Progress");
        Organization organization = organizationRepo.findOne(organizationId);
        
        Submission submission = new Submission(submitter, organization);
        submission.setState(startingState);

        //Clone (as SubmissionWorkflowSteps) all the aggregate workflow steps of the requesting org
        for(WorkflowStep aws : organization.getAggregateWorkflowSteps()) {
            SubmissionWorkflowStep submissionWorkflowStep = submissionWorkflowStepRepo.findOrCreate(organization, aws);
            submission.addSubmissionWorkflowStep(submissionWorkflowStep);
        }
        
        return submissionRepo.save(submission);
    }

}
