package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.transaction.annotation.Transactional;

public class FieldProfileTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The repository was not empty!", 0, languageRepo.count());
        assertEquals("The repository was not empty!", 0, fieldProfileRepo.count());
        assertEquals("The repository was not empty!", 0, fieldPredicateRepo.count());
        language = languageRepo.create(TEST_LANGUAGE);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
    }

    @Override
    public void testCreate() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        assertEquals("The repository did not save the entity!", 1, fieldProfileRepo.count());
        assertEquals("The field profile did not contain the correct perdicate value!", fieldPredicate, fieldProfile.getPredicate());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_INPUT_TYPE, fieldProfile.getInputType());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
    }

    @Override
    public void testDuplication() {
        fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        try {
        	fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldProfileRepo.count());
    }

    @Override
    public void testDelete() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("Entity did not delete!", 0, fieldProfileRepo.count());
    }

    @Override
    public void testCascade() {
        // create field profile
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);

        // add glosses and controlled vocabularies
        FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
        FieldGloss severableFieldGloss = fieldGlossRepo.create(TEST_SEVERABLE_FIELD_GLOSS_VALUE, language);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        ControlledVocabulary severablecontrolledVocabulary = controlledVocabularyRepo.create(TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME, language);
        fieldProfile.addFieldGloss(fieldGloss);
        fieldProfile.addControlledVocabulary(controlledVocabulary);
        fieldProfile.addFieldGloss(severableFieldGloss);
        fieldProfile.addControlledVocabulary(severablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);

        // verify field glosses and controlled vocabularies
        assertEquals("The field profile did not contain the correct field gloss!", fieldGloss, (FieldGloss) fieldProfile.getFieldGlosses().toArray()[0]);
        assertEquals("The field profile did not contain the correct severable field gloss!", severableFieldGloss, (FieldGloss) fieldProfile.getFieldGlosses().toArray()[1]);
        assertEquals("The field profile did not contain the correct controlled vocabulary!", controlledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals("The field profile did not contain the correct severable controlled vocabulary!", severablecontrolledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME));

        // test remove severable gloss
        fieldProfile.removeFieldGloss(severableFieldGloss);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getFieldGlosses().size());

        // test remove severable controlled vocabularies
        fieldProfile.removeControlledVocabulary(severablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getControlledVocabularies().size());

        // test delete profile
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("An field profile was not deleted!", 0, fieldProfileRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
        assertEquals("The field predicate was deleted!", 1, fieldPredicateRepo.count());
        assertEquals("The field glosses were deleted!", 2, fieldGlossRepo.count());
        assertEquals("The controlled vocabularies were deleted!", 2, controlledVocabularyRepo.count());

    }
    
    
    @Test
    public void testInheritFieldProfileViaPointer() {
    	
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        
        
        Organization grandchildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());
        
        childOrganization.addChildOrganization(grandchildOrganization);
        childOrganization = organizationRepo.save(childOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);
        
        FieldProfile fieldProfile = fieldProfileRepo.create(parentWorkflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());
        
        
        FieldProfile parentFieldProfile = parentOrganization.getWorkflow().get(0).getFieldProfiles().get(0);
        FieldProfile childFieldProfile = childOrganization.getWorkflow().get(0).getFieldProfiles().get(0);
        FieldProfile grandchildFieldProfile = grandchildOrganization.getWorkflow().get(0).getFieldProfiles().get(0);

        
        assertEquals("The parent organization's workflow did not contain the fieldProfile", fieldProfile.getId(), parentFieldProfile.getId());
        assertEquals("The child organization's workflow did not contain the fieldProfile", fieldProfile.getId(), childFieldProfile.getId());
        assertEquals("The parent organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getPredicate().getId(), parentFieldProfile.getPredicate().getId());
        assertEquals("The child organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getPredicate().getId(), childFieldProfile.getPredicate().getId());
        
        String updatedFieldPredicateValue = "Updated Value";
        parentFieldProfile.getPredicate().setValue(updatedFieldPredicateValue);
        
        
        fieldProfileRepo.save(parentFieldProfile);
        
        
        childFieldProfile = fieldProfileRepo.findOne(childFieldProfile.getId());
        grandchildFieldProfile = fieldProfileRepo.findOne(grandchildFieldProfile.getId());
        
        
        assertEquals("The child fieldProfile's value did not recieve updated value", updatedFieldPredicateValue, childFieldProfile.getPredicate().getValue());
        assertEquals("The grand child fieldProfile's value did not recieve updated value", updatedFieldPredicateValue, grandchildFieldProfile.getPredicate().getValue());
    }
        
    @Test
    public void testCanOverrideNonOverrideableAtOriginatingOrg() {
    	
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);
        
        FieldProfile fieldProfile = fieldProfileRepo.create(parentWorkflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_NONOVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        
        String helpTest = "Help!";
        
        fieldProfile.setOverrideable(false);
        fieldProfile.setHelp(helpTest);

        
        assertTrue("The setter didn't work for help string on the FieldProfile!", fieldProfile.getHelp().equals(helpTest));
        
        assertFalse("The field profile didn't record that it was made non-overrideable!", fieldProfile.getOverrideable());
        
        fieldProfile = fieldProfileRepo.update(fieldProfile, parentOrganization);
        
        assertTrue("The field profile wasn't updated to include the changed help!", fieldProfile.getHelp().equals("Help!"));
        
    }
    
    @Test
    public void testFieldProfileChangeAtChildOrg() {
    	
    	// this test calls for adding a single workflowstep to the parent organization
    	workflowStepRepo.delete(workflowStep);
    	
    	
    	Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	organization = organizationRepo.findOne(organization.getId());
    	
    	parentOrganization.addChildOrganization(organization);
    	parentOrganization = organizationRepo.save(parentOrganization);
    	
    	organization = organizationRepo.findOne(organization.getId());
      
    	Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	organization = organizationRepo.findOne(organization.getId());
    	
    	organization.addChildOrganization(grandChildOrganization);
    	organization = organizationRepo.save(organization);
    	
    	
    	Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	
    	greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	
    	grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
    	grandChildOrganization = organizationRepo.save(grandChildOrganization);
    	
    	Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	
    	anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	
    	grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
    	grandChildOrganization = organizationRepo.save(grandChildOrganization);
    	
    	
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	
    	WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
    	
    	FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_NONOVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    	
    	Long originalFieldProfileId = fieldProfile.getId();
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	organization = organizationRepo.findOne(organization.getId());
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
    	anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
    	
    	
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		        
		assertEquals("Parent organization has the incorrect number of workflow steps!", 1, parentOrganization.getWorkflowSteps().size());
		assertEquals("Parent organization has wrong size of workflow!", 1, parentOrganization.getWorkflow().size());
		
		assertEquals("organization has the incorrect number of workflow steps!", 0, organization.getWorkflowSteps().size());
		assertEquals("organization has wrong size of workflow!", 1, organization.getWorkflow().size());
		
		assertEquals("Grand child organization has the incorrect number of workflow steps!", 0, grandChildOrganization.getWorkflowSteps().size());
		assertEquals("Grand child organization has wrong size of workflow!", 1, grandChildOrganization.getWorkflow().size());
		
		assertEquals("Great grand child organization has the incorrect number of workflow steps!", 0, greatGrandChildOrganization.getWorkflowSteps().size());
		assertEquals("Great grand child organization has wrong size of workflow!", 1, greatGrandChildOrganization.getWorkflow().size());
		
		assertEquals("Another great grand child organization has the incorrect number of workflow steps!", 0, anotherGreatGrandChildOrganization.getWorkflowSteps().size());
		assertEquals("Another great grand child organization has wrong size of workflow!", 1, anotherGreatGrandChildOrganization.getWorkflow().size());
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	
    	

    	fieldProfile.setHelp("Changed Help Message");
      
    	//request the change at the level of the child organization        
    	FieldProfile updatedFieldProfile = fieldProfileRepo.update(fieldProfile, organization);
    	
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	organization = organizationRepo.findOne(organization.getId());
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
    	anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
    	
    	// pointer to fieldProfile became updatedFieldProfile, must fetch it agains
    	fieldProfile = fieldProfileRepo.findOne(originalFieldProfileId);
      
    	//There should be a new workflow step on the child organization that is distinct from the original workflowStep
    	WorkflowStep updatedWorkflowStep = organization.getWorkflow().get(0);
    	assertFalse("The updatedWorkflowStep was just the same as the original from which it was derived when its field profile was updated!", workflowStep.getId().equals(updatedWorkflowStep.getId()));
      
    	//The new workflow step should contain the new updatedFieldProfile
    	assertTrue("The updatedWorkflowStep didn't contain the new updatedFieldProfile", updatedWorkflowStep.getFields().contains(updatedFieldProfile));
      
    	//The updatedFieldProfile should be distinct from the original fieldProfile
    	assertFalse("The updatedFieldProfile was just the same as the original from which it was derived!", fieldProfile.getId().equals(updatedFieldProfile.getId()));
      
    	//the grandchild and great grandchildren should all be using the new workflow step and the updatedFieldProfile
    	assertTrue("The grandchild org didn't have the updatedWorkflowStep!", grandChildOrganization.getWorkflow().contains(updatedWorkflowStep));
    	assertTrue("The grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!", grandChildOrganization.getWorkflow().get(0).getFields().contains(updatedFieldProfile));
    	assertTrue("The great grandchild org didn't have the updatedWorkflowStep!", greatGrandChildOrganization.getWorkflow().contains(updatedWorkflowStep));
    	assertTrue("The great grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!", greatGrandChildOrganization.getWorkflow().get(0).getFields().contains(updatedFieldProfile));
    	assertTrue("Another great grandchild org didn't have the updatedWorkflowStep!", anotherGreatGrandChildOrganization.getWorkflow().contains(updatedWorkflowStep));
    	assertTrue("Another great grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!", anotherGreatGrandChildOrganization.getWorkflow().get(0).getFields().contains(updatedFieldProfile));
    }
 
    @Test
    public void testMaintainFieldOrderWhenOverriding() {
      
    	Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	
    	parentOrganization.addChildOrganization(organization);
    	parentOrganization = organizationRepo.save(parentOrganization);
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	organization = organizationRepo.findOne(organization.getId());
      
    	
    	Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	
    	organization.addChildOrganization(grandChildOrganization);
    	organization = organizationRepo.save(organization);
    	
    	grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
    	
      
    	
    	Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
    	
    	grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
    	grandChildOrganization = organizationRepo.save(grandChildOrganization);
    	
    	greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
    	
      
    	Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
    	parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    	
    	anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
    	
    	grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
    	grandChildOrganization = organizationRepo.save(grandChildOrganization);;
    	
    	anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
    	
    	
    	parentOrganization = organizationRepo.findOne(parentOrganization.getId());
    	
    	
    	WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
    	
    	FieldProfile fp1 = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_NONOVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
    	workflowStep = workflowStepRepo.findOne(workflowStep.getId());
    	fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());
    	
    	// violates unique constraints, needs either different workflowStep or fieldPredicate
//    	FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_NONOVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
//    	workflowStep = workflowStepRepo.findOne(workflowStep.getId());
//    	fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());
    	
    	// violates unique constraints, needs either different workflowStep or fieldPredicate
//    	FieldProfile fp3 = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_NONOVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
//    	workflowStep = workflowStepRepo.findOne(workflowStep.getId());
//    	fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());
    }
    
    @Test
    public void testMakeFieldNonOverrideable() {
      
    }
    
    @After
    public void cleanUp() {
        
    	fieldProfileRepo.findAll().forEach(fieldProfile -> {
    		fieldProfileRepo.delete(fieldProfile);
        });
    	
    	workflowStepRepo.findAll().forEach(workflowStep -> {
        	workflowStepRepo.delete(workflowStep);
        });
    	
        organizationCategoryRepo.deleteAll();
        
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        
        fieldPredicateRepo.deleteAll();
        fieldGlossRepo.deleteAll();        
        controlledVocabularyRepo.deleteAll();
        languageRepo.deleteAll();
    }
    
}
