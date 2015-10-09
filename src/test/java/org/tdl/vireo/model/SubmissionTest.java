package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Calendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.AttachmentRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubmissionTest {

    private static final String TEST_PARENT_SUBMISSION_SUBMITTER_EMAIL = "admin@tdl.org";
    private static final String TEST_PARENT_SUBMISSION_SUBMITTER_FIRSTNAME = "TDL";
    private static final String TEST_PARENT_SUBMISSION_SUBMITTER_LASTNAME = "Admin";
    private static final Role TEST_PARENT_SUBMISSION_SUBMITTER_ROLE = Role.ADMINISTRATOR;

    private static final String TEST_PARENT_SUBMISSION_STATE_NAME = "Test Parent Submission State";

    private static final boolean TEST_PARENT_SUBMISSION_STATE_ARCHIVED = true;
    private static final boolean TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE = true;
    private static final boolean TEST_PARENT_SUBMISSION_STATE_DELETABLE = true;
    private static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    private static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    private static final boolean TEST_PARENT_SUBMISSION_STATE_ACTIVE = true;

    private static final String TEST_PARENT_FIELD_PREDICATE_VALUE = "dc.whatever";
    private static final String TEST_DETACHABLE_FIELD_PREDICATE_VALUE = "dc.detachable";

    private static final String TEST_PARENT_FIELD_VALUE = "Test Field Value";

    private static final String TEST_PARENT_CATEGORY_NAME = "Test Parent Category";

    private static final int TEST_PARENT_CATEGORY_LEVEL = 0;

    private static final String TEST_PARENT_ORGANIZATION_NAME = "Test Parent Organization";
    private static final String TEST_DETACHABLE_ORGANIZATION_NAME = "Test Detachable Organization";

    private static final String TEST_PARENT_WORKFLOW_STEP_NAME = "Test Parent Workflow Step";
    private static final String TEST_DETACHABLE_WORKFLOW_STEP_NAME = "Test Detachable Workflow Step";
    
    private static final String TEST_PARENT_SUBMISSION_STATE_ACTION_LOG_ENTRY = "Test ActionLog Entry";
    private static final boolean TEST_PARENT_SUBMISSION_STATE_ACTION_LOG_FLAG = true;
    private Calendar TEST_PARENT_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE;
    
    private static final String TEST_ATTACHMENT_NAME = "Test Attachment Name";
    private UUID TEST_UUID = UUID.randomUUID();
    private Calendar TEST_ATTACHMENT_DATE;

    private static User parentSubmissionSubmitter;
    private static SubmissionState parentSubmissionState;
    private static FieldPredicate parentFieldPredicate;
    private static FieldValue parentFieldValue;
    private static Organization parentOrganization;
    private static WorkflowStep parentSubmissionWorkflowStep;
    private static Attachment testAttachment;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private ActionLogRepo actionLogRepo;
    
    @Autowired
    private AttachmentRepo attachmentRepo;

    @Before
    public void setUp() {
        assertEquals("The submission repository was not empty!", 0, submissionRepo.count());

        parentSubmissionSubmitter = userRepo.create(TEST_PARENT_SUBMISSION_SUBMITTER_EMAIL, TEST_PARENT_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_PARENT_SUBMISSION_SUBMITTER_LASTNAME, TEST_PARENT_SUBMISSION_SUBMITTER_ROLE);
        assertEquals("The user does not exist!", 1, userRepo.count());

        parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submission state does not exist!", 1, submissionStateRepo.count());

        parentFieldPredicate = fieldPredicateRepo.create(TEST_PARENT_FIELD_PREDICATE_VALUE);
        assertEquals("The field predicate does not exist!", 1, fieldPredicateRepo.count());

        parentFieldValue = fieldValueRepo.create(parentFieldPredicate);
        parentFieldValue.setValue(TEST_PARENT_FIELD_VALUE);
        parentFieldValue = fieldValueRepo.save(parentFieldValue);
        assertEquals("The field value does not exist!", 1, fieldValueRepo.count());
        assertEquals("The field value did not have the correct value!", TEST_PARENT_FIELD_VALUE, parentFieldValue.getValue());

        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME, TEST_PARENT_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        parentSubmissionWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME);
        assertEquals("The workflow step does not exist!", 1, workflowStepRepo.count());
        
       // testAttachment = attachmentRepo.create();
       // assertEquals("The attachment is created ",1,attachmentRepo.count());
    }

    @Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
        Submission parentSubmission = submissionRepo.create(parentSubmissionSubmitter, parentSubmissionState);
        parentSubmission.addOrganization(parentOrganization);
        parentSubmission.addSubmissionWorkflowStep(parentSubmissionWorkflowStep);
        parentSubmission.addFieldValue(parentFieldValue);
        parentSubmission = submissionRepo.save(parentSubmission);

        assertEquals("The repository did not save the submission!", 1, submissionRepo.count());
        assertEquals("Saved submission did not contain the correct state!", parentSubmissionState, parentSubmission.getState());
        assertEquals("Saved submission did not contain the correct submitter!", parentSubmissionSubmitter, parentSubmission.getSubmitter());
        assertEquals("Saved submission did not contain the correct organization!", true, parentSubmission.getOrganizations().contains(parentOrganization));
        assertEquals("Saved submission did not contain the correct submission workflow step!", true, parentSubmission.getSubmissionWorkflowSteps().contains(parentSubmissionWorkflowStep));
        assertEquals("Saved submission did not contain the correct field value!", true, parentSubmission.getFieldValues().contains(parentFieldValue));
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        submissionRepo.create(parentSubmissionSubmitter, parentSubmissionState);
        assertEquals("The repository didn't persist submission!", 1, submissionRepo.count());
        try {
            submissionRepo.create(parentSubmissionSubmitter, parentSubmissionState);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated the submission!", 1, submissionRepo.count());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        submissionRepo.create(parentSubmissionSubmitter, parentSubmissionState);
        Submission parentSubmission = submissionRepo.findBySubmitterAndState(parentSubmissionSubmitter, parentSubmissionState);
        assertNotEquals("Did not find submission!", null, parentSubmission);
        assertEquals("Found submission did not contain the correct state!", parentSubmissionState, parentSubmission.getState());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        Submission parentSubmission = submissionRepo.create(parentSubmissionSubmitter, parentSubmissionState);
        submissionRepo.delete(parentSubmission);
        assertEquals("Submission did not delete!", 0, submissionRepo.count());
    }

    @Test
    @Order(value = 5)
    public void testCascade() {
        Organization detachableOrganization = organizationRepo.create(TEST_DETACHABLE_ORGANIZATION_NAME, parentOrganization.getCategory());

        WorkflowStep detachableWorkflowStep = workflowStepRepo.create(TEST_DETACHABLE_WORKFLOW_STEP_NAME);

        FieldPredicate detachableFieldPredicate = fieldPredicateRepo.create(TEST_DETACHABLE_FIELD_PREDICATE_VALUE);
        FieldValue detachableFieldValue = fieldValueRepo.create(detachableFieldPredicate);

        Submission parentSubmission = submissionRepo.create(parentSubmissionSubmitter, parentSubmissionState);
        SubmissionState testSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        //Attachment testAttachment = attachmentRepo.create();
       // ActionLog testActionLog = actionLogRepo.create(parentSubmission, testSubmissionState, parentSubmissionSubmitter, TEST_PARENT_SUBMISSION_STATE_ACTION_LOG_ACTION_DATE,testAttachment, TEST_PARENT_SUBMISSION_STATE_ACTION_LOG_ENTRY, TEST_PARENT_SUBMISSION_STATE_ACTION_LOG_FLAG) ;
        		
        parentSubmission.addOrganization(parentOrganization);
        parentSubmission.addSubmissionWorkflowStep(parentSubmissionWorkflowStep);
        parentSubmission.addFieldValue(parentFieldValue);
        parentSubmission.addOrganization(detachableOrganization);
        parentSubmission.addSubmissionWorkflowStep(detachableWorkflowStep);
        parentSubmission.addFieldValue(detachableFieldValue);
       // parentSubmission.addActionLog(testActionLog);
        //parentSubmission.addAttachment(testAttachment);
        parentSubmission = submissionRepo.save(parentSubmission);

        //TODO:  These are not JPA detachments from an EM!!!
        // test detach organization in the sense of not being associated with that organization anymore
        parentSubmission.removeOrganization(detachableOrganization);
        parentSubmission = submissionRepo.save(parentSubmission);
        assertEquals("The organization was not detached!", 1, parentSubmission.getOrganizations().size());
        assertEquals("The organization was deleted!", 2, organizationRepo.count());

        // test detach workflow step
        parentSubmission.removeSubmissionWorkflowStep(detachableWorkflowStep);
        parentSubmission = submissionRepo.save(parentSubmission);
        assertEquals("The workflow step was not detached!", 1, parentSubmission.getSubmissionWorkflowSteps().size());
        assertEquals("The workflow step was deleted!", 1, workflowStepRepo.count());

        // test detach field value
        parentSubmission.removeFieldValue(detachableFieldValue);
        parentSubmission = submissionRepo.save(parentSubmission);
        assertEquals("The field value was not detached!", 1, parentSubmission.getFieldValues().size());
        assertEquals("The field value was orphaned!", 1, fieldValueRepo.count());
        
        // test detach actionLog
       // parentSubmission.removeActionLog(testActionLog);
       // parentSubmission = submissionRepo.save(parentSubmission);
        //assertEquals("The actionLog entry was not detached", 1, parentSubmission.getActionLog().size());
        //assertEquals("The actionLog entry was orphaned", 1, actionLogRepo.count());
        
        //test detach Attachment
        /*parentSubmission.removeAttachment(testAttachment);
        parentSubmission = submissionRepo.save(parentSubmission);
        assertEquals("The attachment was not detached", 1, parentSubmission.getAttachments().size());
        assertEquals("The attachment entry was orphaned", 1, attachmentRepo.count());*/
        
        // test delete submission
        submissionRepo.delete(parentSubmission);
        assertEquals("Submission was deleted!", 0, submissionRepo.count());
        assertEquals("The submission state was deleted!", 1, submissionStateRepo.count());
        assertEquals("The organization was deleted!", 2, organizationRepo.count());
        assertEquals("The workflow steps were deleted!", 0, workflowStepRepo.count());
        assertEquals("The field values were orphaned!", 0, fieldValueRepo.count());
        assertEquals("The field predicates were orphaned!", 2, fieldPredicateRepo.count());
    }

    @After
    public void cleanUp() {
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();
        workflowStepRepo.deleteAll();
        fieldValueRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        userRepo.deleteAll();
    }
}
