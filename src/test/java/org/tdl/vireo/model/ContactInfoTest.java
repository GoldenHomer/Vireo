package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ContactInfoTest extends AbstractEntityTest {

	@Before
	public void setUp() {
		assertEquals("The contactInfo repository is not empty!", 0, contactInfoRepo.count());

		testAddress = addressRepo.create(TEST_ADDRESS1, TEST_ADDRESS2, TEST_CITY, TEST_STATE, TEST_POSTAL_CODE,
				TEST_COUNTRY);
		assertEquals("The address dependency was not created successfully!", 1, addressRepo.count());
	}

	@Test
	@Order(value = 1)
	@Transactional
	public void testCreate() {
		ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		assertEquals("The contact info is not created", 1, contactInfoRepo.count());
		assertEquals("Created testContactInfo does not contain the correct address", testAddress,
				testContactInfo.getAddress());
		assertEquals("Created testContactInfo does not contain the correct phone ", TEST_PHONE,
				testContactInfo.getPhone());
		assertEquals("Created testContactInfo does not contain the correct email ", TEST_EMAIL,
				testContactInfo.getEmail());
	}

	@Test
	@Order(value = 2)
	@Transactional
	public void testDuplication() {
		contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		assertEquals("Duplicate contact info entry is not saved", 2, contactInfoRepo.count());
	}

	@Test
	@Order(value = 3)
	@Transactional
	public void testDelete() {
		ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		contactInfoRepo.delete(testContactInfo);
		assertEquals("The contact info was not deleted", 0, contactInfoRepo.count());
	}

	@Override
	public void testFind() {
	}

	@Test
	@Order(value = 4)
	@Transactional
	public void testCascade() {
		ContactInfo testContactInfo = contactInfoRepo.create(testAddress, TEST_PHONE, TEST_EMAIL);
		contactInfoRepo.delete(testContactInfo);
		assertEquals("Cascade delete did not happen for address", 0, addressRepo.count());
	}

	@After
	public void cleanUp() {
		addressRepo.deleteAll();
		contactInfoRepo.deleteAll();
	}

}
