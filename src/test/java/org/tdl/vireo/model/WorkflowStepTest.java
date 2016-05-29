package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class WorkflowStepTest extends AbstractEntityTest {

    @Before
    public void setup() {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
    }
    
    @Override
    public void testCreate() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        assertEquals("The repository did not save the entity!", 1, workflowStepRepo.count());
        assertEquals("Saved entity did not contain the name!", TEST_WORKFLOW_STEP_NAME, workflowStep.getName());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_INPUT_TYPE, fieldProfile.getInputType());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_OVERRIDEABLE, fieldProfile.getOverrideable());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("The field profile did not contain the correct value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getPredicate());
    }
    
    @Override
    public void testDelete() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        workflowStepRepo.delete(workflowStep);
        assertEquals("Entity did not delete!", 0, workflowStepRepo.count());
    }
    
    @Override
    public void testDuplication() {}

    @Override
    public void testCascade() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
        
        Note note = noteRepo.create(TEST_NOTE_NAME, TEST_NOTE_TEXT);
        Note noteToDisassociate = noteRepo.create(TEST_SEVERABLE_NOTE_NAME, TEST_SEVERABLE_NOTE_TEXT);
        
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
        FieldPredicate fieldPredicateToDisassociate = fieldPredicateRepo.create(TEST_SEVERABLE_FIELD_PREDICATE_VALUE);
        
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE,  TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        FieldProfile fieldProfileToDisassociate = fieldProfileRepo.create(workflowStep, fieldPredicateToDisassociate, TEST_SEVERABLE_FIELD_PROFILE_INPUT_TYPE, TEST_SEVERABLE_FIELD_PROFILE_USAGE, TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE,  TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_SEVERABLE_FIELD_PROFILE_ENABLED, TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        
        workflowStep.addNote(note);
        workflowStep.addNote(noteToDisassociate);
        
        
        // TODO replace with update
        workflowStep = workflowStepRepo.save(workflowStep);
        
        
        // check number of field profiles
        assertEquals("Saved entity did not contain the correct number of field profiles!", 2, workflowStep.getFieldProfiles().size());
        assertEquals("WorkflowStep repo does not have the correct number of field profiles", 2, fieldProfileRepo.count());
        
        // check number of notes
        assertEquals("WorkflowStep repo does not have the correct number of notes", 2, workflowStep.getNotes().size());

        // check number of field predicates
        assertEquals("WorkflowStep repo does not have the correct number of field profiles", 2, workflowStep.getFieldProfiles().size());

        // verify field profiles
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(fieldPredicate).getRepeatable());
        assertEquals("Saved entity did not contain the field profile enabled value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("Saved entity did not contain the field profile optional value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
        assertEquals("Saved entity did not contain the field profile input type!", TEST_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileByPredicate(fieldPredicate).getInputType());
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_SEVERABLE_FIELD_PROFILE_USAGE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getUsage());
        assertEquals("Saved entity did not contain the field profile repeatable value!", TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getRepeatable());
        assertEquals("Saved entity did not contain the field profile required value!", TEST_SEVERABLE_FIELD_PROFILE_ENABLED, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getEnabled());
        assertEquals("Saved entity did not contain the field profile required value!", TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getOptional());
        assertEquals("Saved entity did not contain the field profile input type!", TEST_SEVERABLE_FIELD_PROFILE_INPUT_TYPE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getInputType());

        // verify field predicates
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getPredicate());
        assertEquals("Saved entity did not contain the field profile field predicate value!", fieldPredicateToDisassociate, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getPredicate());

        
        
        // test remove field profile from workflowStep
        workflowStep.removeFieldProfile(fieldProfileToDisassociate);
                 
        // TODO: replace with update
        workflowStep = workflowStepRepo.save(workflowStep);
        
        
        //the field profile should no longer be on the workflow step, and it should be deleted since it was orphaned
        assertEquals("The field profile was not removed!", false, workflowStep.getFieldProfiles().contains(fieldProfileToDisassociate));
        assertEquals("The field profile was deleted!", 2, fieldProfileRepo.count());
        
        
        // test remove note from workflow step
        workflowStep.removeNote(noteToDisassociate);
        
        long noteCount = noteRepo.count();
        
        // TODO: replace with update
        workflowStep = workflowStepRepo.save(workflowStep);
        
        //the note should no longer be on the workflow step, but it should not be deleted
        assertEquals("The note was not removed!", 1, workflowStep.getNotes().size());
        assertEquals("The note was deleted!", noteCount, noteRepo.count());
        
        // test delete workflow step
        workflowStepRepo.delete(workflowStep);
        
        // assert workflow step was deleted
        assertEquals("The workflow step was not deleted!", 0, workflowStepRepo.count());
        
        // assert all field profiles originating in the workflow step are deleted,
        // but the many-to-many associated properties are not
        assertEquals("The field profiles originating in this workflow step were orphaned!", 0, fieldProfileRepo.count());
        assertEquals("The notes were deleted!", noteCount, noteRepo.count());
        assertEquals("The field predicates were deleted!", 2, fieldPredicateRepo.count());
    }
    
    
    @Test
    public void testWorkFlowStepDefaultEmptyInit() {
        Organization org = organizationRepo.create("testOrg", parentCategory);
        assertEquals("A newly created organization should have no workflow steps", 0, org.getWorkflowSteps().size());
        assertEquals("A newly created organization should have empty workflow", 0, org.getWorkflow().size());
    }
    
    @Test
    public void testWorkFlowStepAppend() {
        Organization organization = organizationRepo.create("testOrg", parentCategory);
        workflowStepRepo.create("first step", organization);        
        organization = organizationRepo.findOne(organization.getId());        
        assertEquals("The organization should have one step", 1, organization.getWorkflowSteps().size());
        assertEquals("The organization should have one step in workflow", 1, organization.getWorkflow().size());
        
        workflowStepRepo.create("second step", organization);
        assertEquals("The organization should have one step", 2, organization.getWorkflowSteps().size());
        assertEquals("The organization should have one step in workflow", 2, organization.getWorkflow().size());
    }
    
    @Test
    public void testWorkFlowStepAppendAtIndexSuccess() {
        workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 1 workflow steps.", 1, organization.getWorkflowSteps().size());
        
        workflowStepRepo.create("second step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 2 workflow steps.", 2, organization.getWorkflowSteps().size());
        
        workflowStepRepo.create("third step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 3 workflow steps.", 3, organization.getWorkflowSteps().size());
        
        workflowStepRepo.create("fourth step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 4 workflow steps.", 4, organization.getWorkflowSteps().size());
        
        workflowStepRepo.create("fifth step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 5 workflow steps.", 5, organization.getWorkflowSteps().size());
    }
    
    @Test
    public void testWorkFlowOrderRecordsCorrectly() {
        WorkflowStep ws1 = workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 1 workflow steps.", 1, organization.getWorkflowSteps().size());
        
        WorkflowStep ws2 = workflowStepRepo.create("second step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 2 workflow steps.", 2, organization.getWorkflowSteps().size());
        
        WorkflowStep ws3 = workflowStepRepo.create("third step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 3 workflow steps.", 3, organization.getWorkflowSteps().size());
        
        WorkflowStep ws4 = workflowStepRepo.create("fourth step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 4 workflow steps.", 4, organization.getWorkflowSteps().size());
        
        WorkflowStep ws5 = workflowStepRepo.create("fifth step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The org should have 5 workflow steps.", 5, organization.getWorkflowSteps().size());
        
        assertEquals("Step 1 did not appear in position 1!", ws1.getId(), organization.getWorkflow().get(0).getId());
        assertEquals("Step 2 did not appear in position 2!", ws2.getId(), organization.getWorkflow().get(1).getId());
        assertEquals("Step 3 did not appear in position 3!", ws3.getId(), organization.getWorkflow().get(2).getId());
        assertEquals("Step 4 did not appear in position 4!", ws4.getId(), organization.getWorkflow().get(3).getId());
        assertEquals("Step 5 did not appear in position 5!", ws5.getId(), organization.getWorkflow().get(4).getId());
    }
    
    @Test
    public void testInheritWorkflowInCorrectOrder() {
        
        WorkflowStep ws1 = workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The organization should have 1 workflow steps.", 1, organization.getWorkflowSteps().size());
        
        WorkflowStep ws2 = workflowStepRepo.create("second step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The organization should have 2 workflow steps.", 2, organization.getWorkflowSteps().size());
        
        WorkflowStep ws3 = workflowStepRepo.create("third step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The organization should have 3 workflow steps.", 3, organization.getWorkflowSteps().size());
        
        WorkflowStep ws4 = workflowStepRepo.create("fourth step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The organization should have 4 workflow steps.", 4, organization.getWorkflowSteps().size());
        
        WorkflowStep ws5 = workflowStepRepo.create("fifth step", organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The organization should have 5 workflow steps.", 5, organization.getWorkflowSteps().size());
        
        assertEquals("Organization workflow was the wrong length!", 5, organization.getWorkflow().size());
        assertEquals("Step 1 did not appear in position 1!", ws1.getId(), organization.getWorkflow().get(0).getId());
        assertEquals("Step 2 did not appear in position 2!", ws2.getId(), organization.getWorkflow().get(1).getId());
        assertEquals("Step 3 did not appear in position 3!", ws3.getId(), organization.getWorkflow().get(2).getId());
        assertEquals("Step 4 did not appear in position 4!", ws4.getId(), organization.getWorkflow().get(3).getId());
        assertEquals("Step 5 did not appear in position 5!", ws5.getId(), organization.getWorkflow().get(4).getId());
        
        Organization childOrg = organizationRepo.create("Child Organization", organization, parentCategory);
        assertEquals("Child organization workflow was the wrong length!", 5, childOrg.getWorkflow().size());
        assertEquals("Step 1 did not appear in position 1!", ws1.getId(), childOrg.getWorkflow().get(0).getId());
        assertEquals("Step 2 did not appear in position 2!", ws2.getId(), childOrg.getWorkflow().get(1).getId());
        assertEquals("Step 3 did not appear in position 3!", ws3.getId(), childOrg.getWorkflow().get(2).getId());
        assertEquals("Step 4 did not appear in position 4!", ws4.getId(), childOrg.getWorkflow().get(3).getId());
        assertEquals("Step 5 did not appear in position 5!", ws5.getId(), childOrg.getWorkflow().get(4).getId());
        
    }
    
    @Test
    public void testInheritWorkflowStepViaPointer() {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        assertEquals("The Parent Organization has workflow steps", 0, parentOrganization.getWorkflowSteps().size());
        assertEquals("The Organization has workflow steps", 0, organization.getWorkflowSteps().size());
        assertEquals("The Grand Child Organization has workflow steps", 0, grandChildOrganization.getWorkflowSteps().size());
        
        assertEquals("The Parent Organization has a step in its workflow", 0, parentOrganization.getWorkflow().size());
        assertEquals("The Organization has a step in its workflow", 0, organization.getWorkflow().size());
        assertEquals("The Grand Child Organization has a step in its workflow", 0, grandChildOrganization.getWorkflow().size());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
       
        assertEquals("The Parent Organization did not add workflow steps", 1, parentOrganization.getWorkflowSteps().size());
        assertEquals("The Organization acquired workflow steps", 0, organization.getWorkflowSteps().size());
        assertEquals("The Grand Child Organization acquired workflow steps", 0, grandChildOrganization.getWorkflowSteps().size());
        
        assertEquals("The Parent Organization did not add step to workflow", 1, parentOrganization.getWorkflow().size());
        assertEquals("The Organization did not inherit workflow", 1, organization.getWorkflow().size());
        assertEquals("The Grand Child Organization did not inherit workflow", 1, grandChildOrganization.getWorkflow().size());
        
        
        Long workflowStepId = workflowStep.getId();
        
        String newName = "A Changed Name";
        workflowStep.setName(newName);
        
        WorkflowStep newWorkflowStep = workflowStepRepo.update(workflowStep, workflowStep.getOriginatingOrganization());
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        
        // get old workflow step back. pointer changed!
        workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        // is indeed a new row!
        assertEquals("The workflow step didn't get a new id! Needs a new row in the table!", workflowStep.getId(), newWorkflowStep.getId());
        
        assertEquals("The workflow step didn't get the updated name!", newName, newWorkflowStep.getName());        
        assertEquals("The parentOrganization organization's workflowStep's name was not updated", newName, parentOrganization.getWorkflow().get(0).getName());
        assertEquals("The organization workflowStep's name was not updated", newName, organization.getWorkflow().get(0).getName());
        assertEquals("The grandChildOrganization workflowStep's name was not updated", newName, grandChildOrganization.getWorkflow().get(0).getName());
    }
    
    @Test
    public void testMaintainHierarchyOnDeletionOfInteriorOrg() {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        organization = organizationRepo.findOne(organization.getId());
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
       
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        assertEquals("The Parent Organization has workflow steps", 0, parentOrganization.getWorkflow().size());
        assertEquals("The Organization has workflow steps", 0, organization.getWorkflow().size());
        assertEquals("The Grand Child Organization has workflow steps", 0, grandChildOrganization.getWorkflow().size());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
      
  
        Long workflowStepId = workflowStep.getId();
        
        
        organization = organizationRepo.findOne(organization.getId());
        
        
        //Delete the interior organization
        organizationRepo.delete(organization);
        
        
        workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        //Check that hierarchy is maintained and grandchild is moved to be child of the top
        assertTrue("The hierarchy was not maintained!" , parentOrganization.getChildrenOrganizations().contains(grandChildOrganization));
        assertTrue("The hierarchy was not maintained!" , grandChildOrganization.getParentOrganizations().contains(parentOrganization));
       
        // Check that removal of middle organization does not disturb the grandchild's and Parent's workflow.
        assertEquals("The workflowstep repo didn't contain the single workflow step!", 1, workflowStepRepo.count());
        assertTrue("The Parent Organization doesn't contain workflowStep", parentOrganization.getWorkflow().contains(workflowStep));
        assertTrue("The Grand Child Organization doesn't contain workflowStep", grandChildOrganization.getWorkflow().contains(workflowStep));
        
        
        
        //Check that inheritance still works
        String newName = "A Changed Name";
        workflowStep.setName(newName);
        
        WorkflowStep newWorkflowStep = workflowStepRepo.update(workflowStep, workflowStep.getOriginatingOrganization());
       
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        // get old workflow step back. pointer changed!
        workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        
        // is indeed a new row!
        assertEquals("The workflow step didn't get a new id! Needs a new row in the table!", workflowStep.getId(), newWorkflowStep.getId());
        
        assertEquals("The workflow step didn't get the updated name!", newName, newWorkflowStep.getName());
        assertEquals("The parents organization's workflowStep's name was not updated", newName, parentOrganization.getWorkflow().get(0).getName());
        assertEquals("The grandChildOrganization workflowStep's name was not updated", newName, grandChildOrganization.getWorkflow().get(0).getName());
    }
    
    @Test
    public void testWorkflowStepChangeAtChildOrg() {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        organization = organizationRepo.findOne(organization.getId());
        
        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        
        organization = organizationRepo.findOne(organization.getId());
        
        
        Long workflowStepId = workflowStep.getId();
        
        
        String updatedName = "Updated Name";
        
        workflowStep.setName(updatedName);       
        
       
        WorkflowStep newWorkflowStep = workflowStepRepo.update(workflowStep, organization);
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        
        // get old workflow step back. pointer changed!
        workflowStep = workflowStepRepo.findOne(workflowStepId);
        
        
        //when updating the workflow step at organization, test that
        // a new workflow step is made at the organization
        assertFalse("The child organization did not recieve a new workflowStep; steps has same IDs of " + newWorkflowStep.getId(), newWorkflowStep.getId().equals(workflowStep.getId()));
        assertEquals("The updated workflowStep's name did not change.", updatedName, newWorkflowStep.getName());
        assertEquals("The parent workflowStep's name did change.", TEST_WORKFLOW_STEP_NAME, workflowStep.getName());
        
        // the new workflow step remembers from whence it was derived (the parent's workflow step)
        assertEquals("The child's new workflow step knew not from whence it came", workflowStep.getId(), newWorkflowStep.getOriginatingWorkflowStep().getId());
        
        
        //and furthermore, the organization's descendants point to the new WorkflowStep
        Long grandchildWorkflowStepId = grandChildOrganization.getWorkflow().get(0).getId();
        assertEquals("The grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!", grandchildWorkflowStepId, newWorkflowStep.getId());
        
        
        Long greatGrandChildWorkflowStepId = greatGrandChildOrganization.getWorkflow().get(0).getId();
        assertEquals("The great grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!", greatGrandChildWorkflowStepId, newWorkflowStep.getId());
        
        
        Long anotherGreatGrandChildWorkflowStepId = anotherGreatGrandChildOrganization.getWorkflow().get(0).getId();
        assertEquals("Another great grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!", anotherGreatGrandChildWorkflowStepId, newWorkflowStep.getId());

    }
    
    @Test
    public void testMakeWorkflwoStepWithDescendantsNonOverrideable() {
    	
        //Step S1 has derivative step S2 which has derivative step S3
        //Test that making S1 non-overrideable will blow away S2 and S3 and replace pointer to them with pointers to S1
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        organization = organizationRepo.findOne(organization.getId());
        
        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);
        
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
                
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep t1 = workflowStepRepo.create("Step T", parentOrganization);
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep u1 = workflowStepRepo.create("Step U", parentOrganization);
                
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        
        assertEquals("Parent organization has the incorrect number of workflow steps!", 3, parentOrganization.getWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of workflow!", 3, parentOrganization.getWorkflow().size());
        
        assertEquals("organization has the incorrect number of workflow steps!", 0, organization.getWorkflowSteps().size());
        assertEquals("organization has wrong size of workflow!", 3, organization.getWorkflow().size());
        
        assertEquals("Grand child organization has the incorrect number of workflow steps!", 0, grandChildOrganization.getWorkflowSteps().size());
        assertEquals("Grand child organization has wrong size of workflow!", 3, grandChildOrganization.getWorkflow().size());
        
        assertEquals("Great grand child organization has the incorrect number of workflow steps!", 0, greatGrandChildOrganization.getWorkflowSteps().size());
        assertEquals("Great grand child organization has wrong size of workflow!", 3, greatGrandChildOrganization.getWorkflow().size());
        
        assertEquals("Another great grand child organization has the incorrect number of workflow steps!", 0, anotherGreatGrandChildOrganization.getWorkflowSteps().size());
        assertEquals("Another great grand child organization has wrong size of workflow!", 3, anotherGreatGrandChildOrganization.getWorkflow().size());
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        Long s1Id = s1.getId();

        String updatedName = "Updated Name";
        
        s1.setName(updatedName);
        
        // should change originating organization
        WorkflowStep s2 = workflowStepRepo.update(s1, organization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        // pointer for s1 became s2, have to get from the repo again
        s1 = workflowStepRepo.findOne(s1Id);
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        assertEquals("New workflow step does not have the correct originating workflow step!", s1.getId(), s2.getOriginatingWorkflowStep().getId());
        
        assertEquals("Parent organization has the incorrect number of workflow steps!", 3, parentOrganization.getWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of workflow!", 3, parentOrganization.getWorkflow().size());
        
        // this is important!
        assertEquals("Organization has the incorrect number of workflow steps!", 1, organization.getWorkflowSteps().size());
        assertEquals("Organization has wrong size of workflow!", 3, organization.getWorkflow().size());
        
        
        assertEquals("s1 has the wrong name!", TEST_WORKFLOW_STEP_NAME, s1.getName());
        assertEquals("s2 has the wrong name!", updatedName, s2.getName());
        assertEquals("s2 has the wrong originating Organization!", organization.getId(), s2.getOriginatingOrganization().getId());
        assertEquals("s2 has the wrong originating WorkflowStep!", s1.getId(), s2.getOriginatingWorkflowStep().getId());
        assertEquals("No workflow steps found originating from s1!", 1, workflowStepRepo.findByOriginatingWorkflowStep(s1).size());
        
        assertFalse("Parent organization somehow contains updated workflow step through inheritence!", parentOrganization.getWorkflow().contains(s2));
        
        assertTrue("Organization does not contain updated workflow step through inheritence!", organization.getWorkflow().contains(s2));
        assertTrue("Grandchild Organization does not contain updated workflow step through inheritence!", grandChildOrganization.getWorkflow().contains(s2));
        assertTrue("Great Grandchild Organization does not contain updated workflow step through inheritence!", greatGrandChildOrganization.getWorkflow().contains(s2));
        assertTrue("Another Great Grandchild Organization does not contain updated workflow step through inheritence!", anotherGreatGrandChildOrganization.getWorkflow().contains(s2));
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        Long s2Id = s2.getId();
        
        String anotherUpdatedName = "Yet another updated name";
        
        s2.setName(anotherUpdatedName);

        // should change originating organization
        WorkflowStep s3 = workflowStepRepo.update(s2, grandChildOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        // pointer for s2 became s3, have to get from the repo again
        s2 = workflowStepRepo.findOne(s2Id);

        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        assertEquals("New workflow step does not have the correct originating workflow step!", s2.getId(), s3.getOriginatingWorkflowStep().getId());
        
        assertEquals("Parent organization has the incorrect number of workflow steps!", 3, parentOrganization.getWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of workflow!", 3, parentOrganization.getWorkflow().size());
        
        // this is important!
        assertEquals("Grand child organization has the incorrect number of workflow steps!", 1, grandChildOrganization.getWorkflowSteps().size());
        assertEquals("Grand child organization has wrong size of workflow!", 3, grandChildOrganization.getWorkflow().size());
        
        assertEquals("s3 has the wrong name!", anotherUpdatedName, s3.getName());
        assertEquals("s3 has the wrong originating Organization!", grandChildOrganization.getId(), s3.getOriginatingOrganization().getId());
        assertEquals("s2 has the wrong originating WorkflowStep!", s2.getId(), s3.getOriginatingWorkflowStep().getId());
        assertEquals("No workflow steps found originating from s2!", 1, workflowStepRepo.findByOriginatingWorkflowStep(s2).size());
        

        assertEquals("s2 has the wrong originating WorkflowStep!", s2, s3.getOriginatingWorkflowStep());
        assertEquals("No workflow steps found originating from s2!", 1, workflowStepRepo.findByOriginatingWorkflowStep(s2).size());
        
        assertFalse("Parent organization somehow contains updated workflow step through inheritence!", parentOrganization.getWorkflow().contains(s3));
        assertFalse("Organization somehow contains updated workflow step through inheritence!", organization.getWorkflow().contains(s3));
        
        assertTrue("Grandchild Organization does not contain updated workflow step through inheritence!", grandChildOrganization.getWorkflow().contains(s3));
        assertTrue("Great Grandchild Organization does not contain updated workflow step through inheritence!", greatGrandChildOrganization.getWorkflow().contains(s3));
        assertTrue("Another Great Grandchild Organization does not contain updated workflow step through inheritence!", anotherGreatGrandChildOrganization.getWorkflow().contains(s3));
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        
        assertEquals("s1 has the wrong name!", TEST_WORKFLOW_STEP_NAME, s1.getName());
        assertEquals("s2 has the wrong name!", updatedName, s2.getName());
        assertEquals("s2 has the wrong originating Organization!", organization.getId(), s2.getOriginatingOrganization().getId());
        
        assertEquals("s3 has the wrong name!", anotherUpdatedName, s3.getName());
        assertEquals("s3 has the wrong originating Organization!", grandChildOrganization.getId(), s3.getOriginatingOrganization().getId());
        
        
        
        long numWorkflowSteps = workflowStepRepo.count();
        
                
        // now we are ready to make step 1 non-overrideable and ensure that step 2 and 3 go away
                
        s1.setOverrideable(false);
        
        
        
        s1 = workflowStepRepo.update(s1, parentOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        assertEquals("Workflow Step Repo didn't get the disallowed (no longer overrideable) steps deleted!", numWorkflowSteps - 2, workflowStepRepo.count());
        
        assertTrue("Child org didn't get its workflow step replaced by the non-overrideable s1!", organization.getWorkflow().contains(s1));
        assertTrue("Grandchild org didn't get its workflow step replaced by the non-overrideable s1!", grandChildOrganization.getWorkflow().contains(s1));
        assertTrue("Great grandchild org didn't get its workflow step replaced by the non-overrideable s1!", greatGrandChildOrganization.getWorkflow().contains(s1));
        assertTrue("Another Great grandchild org didn't get its workflow step replaced by the non-overrideable s1!", anotherGreatGrandChildOrganization.getWorkflow().contains(s1));
        
        
        assertEquals("Great grandchild org didn't have s1 as the first step", s1.getId(), greatGrandChildOrganization.getWorkflow().get(0).getId());
        assertEquals("Great grandchild org didn't have t1 as the second step", t1.getId(), greatGrandChildOrganization.getWorkflow().get(1).getId());
        assertEquals("Great grandchild org didn't have u1 as the third step", u1.getId(), greatGrandChildOrganization.getWorkflow().get(2).getId());
    }
    
    
    @Test
    public void testDeleteParentWorkflow() {
        
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        parentOrganization.addChildOrganization(organization);        
        parentOrganization = organizationRepo.save(parentOrganization);
        
        
        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        organization = organizationRepo.findOne(organization.getId());
        
        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);
        
        
        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        
        
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        
        grandChildOrganization = organizationRepo.save(grandChildOrganization);
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        
        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        assertEquals("Parent organization has the incorrect number of workflow steps!", 1, parentOrganization.getWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of aggregate workflow!", 1, parentOrganization.getWorkflow().size());
        
        assertEquals("organization has the incorrect number of workflow steps!", 0, organization.getWorkflowSteps().size());
        assertEquals("organization has wrong size of aggregate workflow!", 1, organization.getWorkflow().size());
        
        assertEquals("Grand child organization has the incorrect number of workflow steps!", 0, grandChildOrganization.getWorkflowSteps().size());
        assertEquals("Grand child organization has wrong size of aggregate workflow!", 1, grandChildOrganization.getWorkflow().size());
        
        assertEquals("Great grand child organization has the incorrect number of workflow steps!", 0, greatGrandChildOrganization.getWorkflowSteps().size());
        assertEquals("Great grand child organization has wrong size of aggregate workflow!", 1, greatGrandChildOrganization.getWorkflow().size());
        
        assertEquals("Another great grand child organization has the incorrect number of workflow steps!", 0, anotherGreatGrandChildOrganization.getWorkflowSteps().size());
        assertEquals("Another great grand child organization has wrong size of aggregate workflow!", 1, anotherGreatGrandChildOrganization.getWorkflow().size());
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
        Long s1Id = s1.getId();

        String updatedName = "Updated Name";
        
        s1.setName(updatedName);
        
        // should change originating organization
        WorkflowStep s2 = workflowStepRepo.update(s1, organization);
        
        
        
        Long s2Id = s2.getId();
        
        String anotherUpdatedName = "Yet another updated name";
        
        s2.setName(anotherUpdatedName);

        // should change originating organization
        WorkflowStep s3 = workflowStepRepo.update(s2, grandChildOrganization);
        
        
        
        // pointer for s1 became s2, have to get from the repo again
        s1 = workflowStepRepo.findOne(s1Id);
        
        
        parentOrganization = organizationRepo.save(parentOrganization);
        
        
        parentOrganization.removeWorkflowStep(s1);
        
        
        parentOrganization = organizationRepo.save(parentOrganization);
        
        
        // would like to have orphanRemoval handle this, but need to trigger it with some cascade
        workflowStepRepo.delete(s1);
        
        
        
        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        
        
        assertEquals("All workflow steps have not been removed!", 0, workflowStepRepo.findAll().size());
        
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                
        assertEquals("Parent organization has the incorrect number of workflow steps!", 0, parentOrganization.getWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of aggregate workflow!", 0, parentOrganization.getWorkflow().size());
        
        assertEquals("organization has the incorrect number of workflow steps!", 0, organization.getWorkflowSteps().size());
        assertEquals("organization has wrong size of aggregate workflow!", 0, organization.getWorkflow().size());
        
        assertEquals("Grand child organization has the incorrect number of workflow steps!", 0, grandChildOrganization.getWorkflowSteps().size());
        assertEquals("Grand child organization has wrong size of aggregate workflow!", 0, grandChildOrganization.getWorkflow().size());
        
        assertEquals("Great grand child organization has the incorrect number of workflow steps!", 0, greatGrandChildOrganization.getWorkflowSteps().size());
        assertEquals("Great grand child organization has wrong size of aggregate workflow!", 0, greatGrandChildOrganization.getWorkflow().size());
        
        assertEquals("Another great grand child organization has the incorrect number of workflow steps!", 0, anotherGreatGrandChildOrganization.getWorkflowSteps().size());
        assertEquals("Another great grand child organization has wrong size of aggregate workflow!", 0, anotherGreatGrandChildOrganization.getWorkflow().size());
        
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
    }


    @After
    public void cleanUp() {
    	
		fieldProfileRepo.findAll().forEach(fieldProfile -> {
			fieldProfileRepo.delete(fieldProfile);
		});
		assertEquals("Couldn't delete all field profiles!", 0, fieldProfileRepo.count());

		workflowStepRepo.findAll().forEach(workflowStep -> {
			workflowStepRepo.delete(workflowStep);
		});
		assertEquals("Couldn't delete all workflow steps!", 0, workflowStepRepo.count());

		organizationCategoryRepo.deleteAll();
		assertEquals("Couldn't delete all organization categories!", 0, organizationCategoryRepo.count());

		organizationRepo.findAll().forEach(organization -> {
			organizationRepo.delete(organization);
		});
		assertEquals("Couldn't delete all organizations", 0, organizationRepo.count());

		fieldPredicateRepo.deleteAll();
		assertEquals("Couldn't delete all predicates!", 0, fieldPredicateRepo.count());

		noteRepo.deleteAll();
		assertEquals("Couldn't delete all notes!", 0, noteRepo.count());
    }
   
}
