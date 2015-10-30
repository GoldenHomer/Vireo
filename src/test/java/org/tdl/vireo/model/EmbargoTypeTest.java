package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

public class EmbargoTypeTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("Ebargo repo was not empty!", 0, embargoTypeRepo.count());
    }

    @Override
    public void testCreate() {
        EmbargoType testEmbargo = embargoTypeRepo.create(TEST_EMBARGO_NAME, TEST_EMBARGO_DESCRIPTION, TEST_EMBARGO_DURATION);
        assertEquals("Embargo Repo did not save the embargo!", 1, embargoTypeRepo.count());
        assertEquals("Embargo Repo did not save the correct embargo name!", TEST_EMBARGO_NAME, testEmbargo.getName());
        assertEquals("Embargo Repo did not save the correct embargo description!", TEST_EMBARGO_DESCRIPTION, testEmbargo.getDescription());
        assertEquals("Embargo Repo did not save the correct embargo duration!", TEST_EMBARGO_DURATION, testEmbargo.getDuration());
    }
    @Override
    public void testDuplication() {
    
    }

    @Override
    public void testDelete() {
    
    }

    @Override
    public void testCascade() {
    
    }

    @After
    public void cleanUp() {
        embargoTypeRepo.deleteAll();
    }
    
}