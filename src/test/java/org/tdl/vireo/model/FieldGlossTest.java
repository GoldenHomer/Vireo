package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.Application;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class FieldGlossTest extends AbstractEntityTest {

	@Before
	public void setUp() {
		language = languageRepo.create(TEST_LANGUAGE);
	}

	@Override
	public void testCreate() {
		FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
		fieldGloss = fieldGlossRepo.save(fieldGloss);
		assertEquals("The repository did not save the entity!", 1, fieldGlossRepo.count());
		assertEquals("Saved entity did not contain the value!", TEST_FIELD_GLOSS_VALUE, fieldGloss.getValue());
		assertEquals("Saved entity did not contain the language!", language, fieldGloss.getLanguage());
	}

	@Override
	public void testDuplication() {
		fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
		try {
			fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
		} catch (DataIntegrityViolationException e) {
			/* SUCCESS */ }
		assertEquals("The repository duplicated entity!", 1, fieldGlossRepo.count());
	}

	@Override
	public void testFind() {
		fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
		FieldGloss fieldGloss = fieldGlossRepo.findByValue(TEST_FIELD_GLOSS_VALUE);
		assertNotEquals("Did not find entity!", null, fieldGloss);
		assertEquals("Saved entity did not contain the value!", TEST_FIELD_GLOSS_VALUE, fieldGloss.getValue());
	}

	@Override
	public void testDelete() {
		FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
		fieldGlossRepo.delete(fieldGloss);
		assertEquals("Entity did not delete!", 0, fieldGlossRepo.count());
	}

	@Override
	public void testCascade() {
		FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
		fieldGlossRepo.delete(fieldGloss);
		assertEquals("The language was deleted!", 1, languageRepo.count());
	}

	@After
	public void cleanUp() {
		fieldGlossRepo.deleteAll();
		languageRepo.deleteAll();
	}

}
