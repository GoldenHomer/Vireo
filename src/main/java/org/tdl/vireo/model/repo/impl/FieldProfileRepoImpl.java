package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

import org.springframework.transaction.annotation.Transactional;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {
	
	@PersistenceContext
    private EntityManager em;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }
    
    public FieldProfile update(FieldProfile fieldProfile, Organization requestingOrganization) {
    	
    	//if the requesting organization does not originate the step that originates the fieldProfile, and it is non-overrideable, then throw an exception.
        boolean requestorOriginatesProfile = false;
        
        for(WorkflowStep workflowStep : requestingOrganization.getWorkflow()) {        	
            //if this step of the requesting organization happens to be the originator of the field profile, and the step also originates in the requesting organization, then this organization truly originates the field profile.
            if(fieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStep.getId()) && requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
                requestorOriginatesProfile = true;
            }
        }
        
        //if the requestor is not the originator and it is not overrideable, we can't make the update
        if(!requestorOriginatesProfile && !fieldProfile.getOverrideable()) {
            
        	// provide feedback of attempt to override non overrideable
        	// exceptions may be of better use for unavoidable error handling
        	
        	//TODO: add non overridable exception and throw it here
        	//throw new FieldProfileNonOverrideableException();
        	
        	return null;
        }
        //if the requestor originates, make the update at the requestor
        else if(requestorOriginatesProfile) {
            // do nothing, just save changes 
        	
        	fieldProfile = fieldProfileRepo.save(fieldProfile);
        }
        //else, it's overrideable and we didn't oringinate it so we need to make a new one that overrides.
        else {
            
        	Long originalFieldProfileId = fieldProfile.getId();
        	
        	List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();
        	List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();
        	
        	for(FieldGloss fg : fieldProfile.getFieldGlosses()) {
        		fieldGlosses.add(fg);            		
        	}
        	
			for(ControlledVocabulary cv : fieldProfile.getControlledVocabularies()) {
				controlledVocabularies.add(cv);
        	}
        	
			fieldProfile.setFieldGlosses(new ArrayList<FieldGloss>());
			fieldProfile.setControlledVocabularies(new ArrayList<ControlledVocabulary>());
        	
        	em.detach(fieldProfile);
        	fieldProfile.setId(null);
            
        	
        	FieldProfile originalFieldProfile = fieldProfileRepo.findOne(originalFieldProfileId);
             
        	
        	WorkflowStep originalOriginatingWorkflowStep = originalFieldProfile.getOriginatingWorkflowStep();
        	
        	// when a organization that did not originate the workflow step needs to update the field profile with the step,
        	// a new workflow step must be created with the requesting organization as the originator        	
        	if(!originalOriginatingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
        		
        		WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(originalOriginatingWorkflowStep, requestingOrganization);
        		
        		fieldProfile.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
        	}
        	
        	fieldProfile.setOriginatingFieldProfile(originalFieldProfile);
             
        	fieldProfile.setFieldGlosses(fieldGlosses);
        	fieldProfile.setControlledVocabularies(controlledVocabularies);
        	
        	
        	fieldProfile = fieldProfileRepo.save(fieldProfile);
        	
        	
        	for(WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, originalFieldProfile)) {
        		workflowStep.replaceProfileInFields(originalFieldProfile, fieldProfile);
        		workflowStepRepo.save(workflowStep);
        	}
        	
        	
        	for(WorkflowStep workflowStep : requestingOrganization.getWorkflow()) {
				if(workflowStep.getFields().contains(originalFieldProfile)) {
					workflowStep.replaceProfileInFields(originalFieldProfile, fieldProfile);
					workflowStepRepo.save(workflowStep);
				}
    		}
			
			//organizationRepo.save(requestingOrganization);
        	
        }
        
        return fieldProfile;

    }

    @Override
    public void delete(FieldProfile fieldProfile) {
    	
    	// allows for delete by iterating through findAll, while still deleting descendents
    	if(fieldProfileRepo.findOne(fieldProfile.getId()) != null) {
        
	    	WorkflowStep originatingWorkflowStep = fieldProfile.getOriginatingWorkflowStep();
	    	
	    	originatingWorkflowStep.removeFieldProfile(fieldProfile);
	    	
	    	if(fieldProfile.getOriginatingFieldProfile() != null) {
	    		fieldProfile.setOriginatingFieldProfile(null);
	        }
	    		    	
	    	fieldProfile.setOriginatingWorkflowStep(null);
	    	
	    	workflowStepRepo.findByFieldsId(fieldProfile.getId()).forEach(workflowStep -> {
	    		workflowStep.removeProfileFromFields(fieldProfile);
	    		workflowStepRepo.save(workflowStep);
	        });
	    	
	    	fieldProfileRepo.findByOriginatingFieldProfile(fieldProfile).forEach(fp -> {
	    		fp.setOriginatingFieldProfile(null);
	        });
	    	
	    	deleteDescendantsOfFieldProfile(fieldProfile);
	    	
	    	fieldProfileRepo.delete(fieldProfile.getId());
    	
    	}
    }
    
    private void deleteDescendantsOfFieldProfile(FieldProfile fieldProfile) {
        fieldProfileRepo.findByOriginatingFieldProfile(fieldProfile).forEach(desendantFieldProfile -> {
    		delete(desendantFieldProfile);
        });
    }
    
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, FieldProfile fieldProfile) {
        List<WorkflowStep> descendantWorkflowStepsContainingFieldProfile = new ArrayList<WorkflowStep>();
        organization.getWorkflow().forEach(ws -> {
        	if(ws.getFields().contains(fieldProfile)) {
            	descendantWorkflowStepsContainingFieldProfile.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
        	descendantWorkflowStepsContainingFieldProfile.addAll(getContainingDescendantWorkflowStep(descendantOrganization, fieldProfile));
        });
        return descendantWorkflowStepsContainingFieldProfile;
    }
    
}
