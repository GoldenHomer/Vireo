package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class FieldProfileTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        language = languageRepo.create(TEST_LANGUAGE);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE);
    }

    @Override
    public void testCreate() {
        FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
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
        fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        try {
            fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldProfileRepo.count());
    }

    @Override
    public void testDelete() {
        FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("Entity did not delete!", 0, fieldProfileRepo.count());
    }

    @Override
    public void testCascade() {
        // create field profile
        FieldProfile fieldProfile = fieldProfileRepo.create(fieldPredicate, TEST_FIELD_PROFILE_INPUT_TYPE, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL);

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

    @After
    public void cleanUp() {
        fieldProfileRepo.deleteAll();
        fieldGlossRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        controlledVocabularyRepo.deleteAll();
        languageRepo.deleteAll();
    }
}
