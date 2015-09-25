package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubmissionTest {
    static final String TEST_PARENT_SUBMISSION_STATE_NAME = "Test Parent Submission State";
    static final boolean TEST_PARENT_SUBMISSION_STATE_ARCHIVED = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_DELETABLE = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT = true;
    static final boolean TEST_PARENT_SUBMISSION_STATE_ACTIVE = true;

    static final String TEST_PARENT_FIELD_PREDICATE_VALUE = "dc.whatever";

    static final String TEST_PARENT_FIELD_VALUE_VALUE = "Test Field Value value";

    static final String TEST_PARENT_CATEGORY_NAME = "Test Parent Category";
    static final int TEST_PARENT_CATEGORY_LEVEL = 0;

    static final String TEST_PARENT_ORGANIZATION_NAME = "Test Parent Organization";

    static final String TEST_PARENT_WORKFLOW_NAME = "Test Parent Workflow";

    SubmissionState parentSubmissionState;
    FieldPredicate parentFieldPredicate;
    FieldProfile parentFieldProfile;
    FieldValue parentFieldValue;
    Organization parentOrganization;
    WorkflowStep parentSubmissionWorkflowStep;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SubmissionStateRepo submissionStateRepo;

    @Autowired
    private FieldValueRepo fieldValueRepo;

    @Autowired
    private FieldPredicateRepo fieldPredicateRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @BeforeClass
    public static void init() {

    }

    @Before
    public void setUp() {
        assertEquals("The submission repository was not empty!", 0, submissionRepo.count());

        parentSubmissionState = submissionStateRepo.create(TEST_PARENT_SUBMISSION_STATE_NAME, TEST_PARENT_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_PARENT_SUBMISSION_STATE_DELETABLE, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_PARENT_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_PARENT_SUBMISSION_STATE_ACTIVE);
        assertEquals("The submission state does not exist!", 1, submissionStateRepo.count());

        parentFieldPredicate = fieldPredicateRepo.create(TEST_PARENT_FIELD_PREDICATE_VALUE);
        assertEquals("The field predicate does not exist!", 1, fieldPredicateRepo.count());

        parentFieldValue = fieldValueRepo.create(parentFieldPredicate);
        parentFieldValue.setValue(TEST_PARENT_FIELD_VALUE_VALUE);
        parentFieldValue = fieldValueRepo.save(parentFieldValue);
        assertEquals("The field value does not exist!", 1, fieldValueRepo.count());
        assertEquals("The field value did not have the correct value!", TEST_PARENT_FIELD_VALUE_VALUE, parentFieldValue.getValue());

        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_PARENT_CATEGORY_NAME, TEST_PARENT_CATEGORY_LEVEL);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        parentSubmissionWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_NAME);
        assertEquals("The workflow step does not exist!", 1, workflowStepRepo.count());
    }

    @Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
        Submission parentSubmission = submissionRepo.create(parentSubmissionState);
        parentSubmission.addOrganization(parentOrganization);
        parentSubmission.addSubmissionWorkflowStep(parentSubmissionWorkflowStep);
        parentSubmission.addFieldValue(parentFieldValue);
        parentSubmission = submissionRepo.save(parentSubmission);

        assertEquals("The repository did not save the submission!", 1, submissionRepo.count());
        assertEquals("Saved submission did not contain the correct state!", parentSubmissionState, parentSubmission.getState());
        assertEquals("Saved submission did not contain the correct organization!", true, parentSubmission.getOrganizations().contains(parentOrganization));
        assertEquals("Saved submission did not contain the correct submission workflow step!", true, parentSubmission.getSubmissionWorkflowSteps().contains(parentSubmissionWorkflowStep));
        assertEquals("Saved submission did not contain the correct field value!", true, parentSubmission.getFieldValues().contains(parentFieldValue));
    }

    @Test
    @Order(value = 2)
    public void testDuplication() {
        submissionRepo.create(parentSubmissionState);
        assertEquals("The repository didn't allow a duplicated submission (with the same state)!", 1, submissionRepo.count());
        submissionRepo.create(parentSubmissionState);
        assertEquals("The repository didn't allow a duplicated submission (with the same state)!", 2, submissionRepo.count());
        
        assertEquals("The repository didn't allow a duplicated submission to share the same state as another!", 1, submissionStateRepo.count());
    }

    @Test
    @Order(value = 3)
    public void testFind() {
        submissionRepo.create(parentSubmissionState);
        Submission parentSubmission = (Submission) submissionRepo.findByState(parentSubmissionState).toArray()[0];
        assertNotEquals("Did not find submission!", null, parentSubmission);
        assertEquals("Found submission did not contain the correct state!", parentSubmissionState, parentSubmission.getState());
    }

    @Test
    @Order(value = 4)
    public void testDelete() {
        Submission parentSubmission = submissionRepo.create(parentSubmissionState);
        submissionRepo.delete(parentSubmission);
        assertEquals("Submission did not delete!", 0, submissionRepo.count());
    }

    @Test
    @Order(value = 5)
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();
        workflowStepRepo.deleteAll();
        fieldValueRepo.deleteAll();
        fieldProfileRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        organizationRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
    }

}
