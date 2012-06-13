package org.tdl.vireo.search.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.model.MockPerson;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.search.ActiveSearchFilter;
import org.tdl.vireo.security.SecurityContext;

import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * Test the UriActiveSearchfilterImpl
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class UriActiveSearchFilterImplTest extends UnitTest {

	// Spring dependencies
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	
	/**
	 * Test that the search filter keeps track of all it's variables without
	 * anything crazy going on... just get/set.
	 */
	@Test
	public void testProperties() {
		
		Date start = new Date(2002,5,1);
		Date end = new Date(2010,5,1);
		
		ActiveSearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		filter.addSearchText("text1");
		filter.addSearchText("text2");
		filter.addState("state1");
		filter.addState("state2");
		filter.addAssignee(MockPerson.getStudent());
		filter.addAssignee(MockPerson.getReviewer());
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.addGraduationMonth(1);
		filter.addGraduationMonth(5);
		filter.addDegree("degree1");
		filter.addDegree("degree2");
		filter.addDepartment("dept1");
		filter.addDepartment("dept2");
		filter.addCollege("college1");
		filter.addCollege("college2");
		filter.addMajor("major1");
		filter.addMajor("major2");
		filter.addDocumentType("doc1");
		filter.addDocumentType("doc2");
		filter.setUMIRelease(true);
		filter.setDateRange(start,end);
		
		// yay, now lets read that back out.
		
		assertEquals("text1",filter.getSearchText().get(0));
		assertEquals("text2",filter.getSearchText().get(1));
		assertEquals("state1",filter.getStates().get(0));
		assertEquals("state2",filter.getStates().get(1));
		assertEquals(MockPerson.getStudent(),filter.getAssignees().get(0));
		assertEquals(MockPerson.getReviewer(),filter.getAssignees().get(1));
		assertEquals(Integer.valueOf(2002),filter.getGraduationYears().get(0));
		assertEquals(Integer.valueOf(2003),filter.getGraduationYears().get(1));
		assertEquals(Integer.valueOf(1),filter.getGraduationMonths().get(0));
		assertEquals(Integer.valueOf(5),filter.getGraduationMonths().get(1));
		assertEquals("degree1",filter.getDegrees().get(0));
		assertEquals("degree2",filter.getDegrees().get(1));
		assertEquals("dept1",filter.getDepartments().get(0));
		assertEquals("dept2",filter.getDepartments().get(1));
		assertEquals("college1",filter.getColleges().get(0));
		assertEquals("college2",filter.getColleges().get(1));
		assertEquals("major1",filter.getMajors().get(0));
		assertEquals("major2",filter.getMajors().get(1));
		assertEquals("doc1",filter.getDocumentTypes().get(0));
		assertEquals("doc2",filter.getDocumentTypes().get(1));
		assertEquals(Boolean.valueOf(true),filter.getUMIRelease());
		assertEquals(start,filter.getDateRangeStart());
		assertEquals(end,filter.getDateRangeEnd());
	}
	
	/**
	 * Test that we can encode a search filter, and then decode it and expect it
	 * to be in the exact same state.
	 */
	@Test
	public void testEncodeAndDecode() {


		Date start = new Date(2002,5,1);
		Date end = new Date(2010,5,1);
		context.turnOffAuthorization();
		Person person1 = personRepo.createPerson("person1", "email1@email", "firstName1", "lastName1", RoleType.STUDENT).save();
		Person person2 = personRepo.createPerson("person2", "email2@email", "firstName2", "lastName2", RoleType.STUDENT).save();

		try {

			ActiveSearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
			filter.addSearchText("text1");
			filter.addSearchText("text2");
			filter.addState("state1");
			filter.addState("state2");
			filter.addAssignee(person1);
			filter.addAssignee(person2);
			filter.addGraduationYear(2002);
			filter.addGraduationYear(2003);
			filter.addGraduationMonth(1);
			filter.addGraduationMonth(5);
			filter.addDegree("degree1");
			filter.addDegree("degree2");
			filter.addDepartment("dept1");
			filter.addDepartment("dept2");
			filter.addCollege("college1");
			filter.addCollege("college2");
			filter.addMajor("major1");
			filter.addMajor("major2");
			filter.addDocumentType("doc1");
			filter.addDocumentType("doc2");
			filter.setUMIRelease(true);
			filter.setDateRange(start,end);

			String encoded = filter.encode();

			// Make sure the encoded stirng is at least plausable.
			assertNotNull(encoded);
			assertFalse(encoded.contains(" "));
			assertFalse(encoded.contains("="));
			assertFalse(encoded.contains("?"));
			assertFalse(encoded.contains("&"));

			// Restore from the encoded version.
			ActiveSearchFilter newFilter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
			newFilter.decode(encoded);

			// yay, now lets read that back out.
			assertEquals("text1",newFilter.getSearchText().get(0));
			assertEquals("text2",newFilter.getSearchText().get(1));
			assertEquals("state1",newFilter.getStates().get(0));
			assertEquals("state2",newFilter.getStates().get(1));
			assertEquals(person1,newFilter.getAssignees().get(0));
			assertEquals(person2,newFilter.getAssignees().get(1));
			assertEquals(Integer.valueOf(2002),newFilter.getGraduationYears().get(0));
			assertEquals(Integer.valueOf(2003),newFilter.getGraduationYears().get(1));
			assertEquals(Integer.valueOf(1),newFilter.getGraduationMonths().get(0));
			assertEquals(Integer.valueOf(5),newFilter.getGraduationMonths().get(1));
			assertEquals("degree1",newFilter.getDegrees().get(0));
			assertEquals("degree2",newFilter.getDegrees().get(1));
			assertEquals("dept1",newFilter.getDepartments().get(0));
			assertEquals("dept2",newFilter.getDepartments().get(1));
			assertEquals("college1",newFilter.getColleges().get(0));
			assertEquals("college2",newFilter.getColleges().get(1));
			assertEquals("major1",newFilter.getMajors().get(0));
			assertEquals("major2",newFilter.getMajors().get(1));
			assertEquals("doc1",newFilter.getDocumentTypes().get(0));
			assertEquals("doc2",newFilter.getDocumentTypes().get(1));
			assertEquals(Boolean.valueOf(true),newFilter.getUMIRelease());
			assertEquals(start,newFilter.getDateRangeStart());
			assertEquals(end,newFilter.getDateRangeEnd());

		} finally {
			person1.delete();
			person2.delete();
			context.restoreAuthorization();
		}
	}

	/**
	 * Test that an empty search filter can also be encoded and decoded.
	 */
	@Test
	public void testEncodeAndDecodeNull() {
		
		ActiveSearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		String encoded = filter.encode();
		
		ActiveSearchFilter newFilter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		newFilter.decode(encoded);
		
		assertEquals(0,newFilter.getSearchText().size());
		assertEquals(0,newFilter.getStates().size());
		assertEquals(0,newFilter.getAssignees().size());
		assertEquals(0,newFilter.getGraduationYears().size());
		assertEquals(0,newFilter.getGraduationMonths().size());
		assertEquals(0,newFilter.getDegrees().size());
		assertEquals(0,newFilter.getDepartments().size());
		assertEquals(0,newFilter.getColleges().size());
		assertEquals(0,newFilter.getMajors().size());
		assertEquals(0,newFilter.getDocumentTypes().size());
		assertEquals(null,newFilter.getUMIRelease());
		assertEquals(null,newFilter.getDateRangeStart());
		assertEquals(null,newFilter.getDateRangeEnd());
	}
	
	/**
	 * Test copying of a search filter
	 */
	@Test
	public void testCopyTo() {
		
		
		Date start = new Date(2002,5,1);
		Date end = new Date(2010,5,1);
		
		ActiveSearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		filter.addSearchText("text1");
		filter.addSearchText("text2");
		filter.addState("state1");
		filter.addState("state2");
		filter.addAssignee(MockPerson.getStudent());
		filter.addAssignee(MockPerson.getReviewer());
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.addGraduationMonth(1);
		filter.addGraduationMonth(5);
		filter.addDegree("degree1");
		filter.addDegree("degree2");
		filter.addDepartment("dept1");
		filter.addDepartment("dept2");
		filter.addCollege("college1");
		filter.addCollege("college2");
		filter.addMajor("major1");
		filter.addMajor("major2");
		filter.addDocumentType("doc1");
		filter.addDocumentType("doc2");
		filter.setUMIRelease(true);
		filter.setDateRange(start,end);
		
		ActiveSearchFilter newFilter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		filter.copyTo(newFilter);
		
		// yay, now lets read that back out.
		assertEquals("text1",newFilter.getSearchText().get(0));
		assertEquals("text2",newFilter.getSearchText().get(1));
		assertEquals("state1",newFilter.getStates().get(0));
		assertEquals("state2",newFilter.getStates().get(1));
		assertEquals(MockPerson.getStudent(),newFilter.getAssignees().get(0));
		assertEquals(MockPerson.getReviewer(),newFilter.getAssignees().get(1));
		assertEquals(Integer.valueOf(2002),newFilter.getGraduationYears().get(0));
		assertEquals(Integer.valueOf(2003),newFilter.getGraduationYears().get(1));
		assertEquals(Integer.valueOf(1),newFilter.getGraduationMonths().get(0));
		assertEquals(Integer.valueOf(5),newFilter.getGraduationMonths().get(1));
		assertEquals("degree1",newFilter.getDegrees().get(0));
		assertEquals("degree2",newFilter.getDegrees().get(1));
		assertEquals("dept1",newFilter.getDepartments().get(0));
		assertEquals("dept2",newFilter.getDepartments().get(1));
		assertEquals("college1",newFilter.getColleges().get(0));
		assertEquals("college2",newFilter.getColleges().get(1));
		assertEquals("major1",newFilter.getMajors().get(0));
		assertEquals("major2",newFilter.getMajors().get(1));
		assertEquals("doc1",newFilter.getDocumentTypes().get(0));
		assertEquals("doc2",newFilter.getDocumentTypes().get(1));
		assertEquals(Boolean.valueOf(true),newFilter.getUMIRelease());
		assertEquals(start,newFilter.getDateRangeStart());
		assertEquals(end,newFilter.getDateRangeEnd());
	}
	
	/**
	 * Test copying of a search filter
	 */
	@Test
	public void testCopyFrom() {
		
		Date start = new Date(2002,5,1);
		Date end = new Date(2010,5,1);
		
		ActiveSearchFilter filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		filter.addSearchText("text1");
		filter.addSearchText("text2");
		filter.addState("state1");
		filter.addState("state2");
		filter.addAssignee(MockPerson.getStudent());
		filter.addAssignee(MockPerson.getReviewer());
		filter.addGraduationYear(2002);
		filter.addGraduationYear(2003);
		filter.addGraduationMonth(1);
		filter.addGraduationMonth(5);
		filter.addDegree("degree1");
		filter.addDegree("degree2");
		filter.addDepartment("dept1");
		filter.addDepartment("dept2");
		filter.addCollege("college1");
		filter.addCollege("college2");
		filter.addMajor("major1");
		filter.addMajor("major2");
		filter.addDocumentType("doc1");
		filter.addDocumentType("doc2");
		filter.setUMIRelease(true);
		filter.setDateRange(start,end);
		
		ActiveSearchFilter newFilter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);
		newFilter.copyFrom(filter);
		
		// yay, now lets read that back out.
		assertEquals("text1",newFilter.getSearchText().get(0));
		assertEquals("text2",newFilter.getSearchText().get(1));
		assertEquals("state1",newFilter.getStates().get(0));
		assertEquals("state2",newFilter.getStates().get(1));
		assertEquals(MockPerson.getStudent(),newFilter.getAssignees().get(0));
		assertEquals(MockPerson.getReviewer(),newFilter.getAssignees().get(1));
		assertEquals(Integer.valueOf(2002),newFilter.getGraduationYears().get(0));
		assertEquals(Integer.valueOf(2003),newFilter.getGraduationYears().get(1));
		assertEquals(Integer.valueOf(1),newFilter.getGraduationMonths().get(0));
		assertEquals(Integer.valueOf(5),newFilter.getGraduationMonths().get(1));
		assertEquals("degree1",newFilter.getDegrees().get(0));
		assertEquals("degree2",newFilter.getDegrees().get(1));
		assertEquals("dept1",newFilter.getDepartments().get(0));
		assertEquals("dept2",newFilter.getDepartments().get(1));
		assertEquals("college1",newFilter.getColleges().get(0));
		assertEquals("college2",newFilter.getColleges().get(1));
		assertEquals("major1",newFilter.getMajors().get(0));
		assertEquals("major2",newFilter.getMajors().get(1));
		assertEquals("doc1",newFilter.getDocumentTypes().get(0));
		assertEquals("doc2",newFilter.getDocumentTypes().get(1));
		assertEquals(Boolean.valueOf(true),newFilter.getUMIRelease());
		assertEquals(start,newFilter.getDateRangeStart());
		assertEquals(end,newFilter.getDateRangeEnd());
	}
	
	/**
	 * Test the internal method to escape values making sure they escape and
	 * unescape correctly.
	 */
	@Test
	public void testEscapping() {
		
		UriActiveSearchFilterImpl filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);

		// Check that the match
		assertEquals("one",filter.unescape(filter.escape("one")));
		assertEquals("one,two",filter.unescape(filter.escape("one,two")));
		assertEquals("one,two,three",filter.unescape(filter.escape("one,two,three")));
		assertEquals("one,,two",filter.unescape(filter.escape("one,,two")));
		assertEquals("one,:,two",filter.unescape(filter.escape("one,:,two")));
		assertEquals(",,one,two,,",filter.unescape(filter.escape(",,one,two,,")));
		assertEquals("::one,two::",filter.unescape(filter.escape("::one,two::")));
		assertEquals("one\\,two",filter.unescape(filter.escape("one\\,two")));
		assertEquals("one%,two",filter.unescape(filter.escape("one%,two")));
		assertEquals("one%2C,two",filter.unescape(filter.escape("one%2C,two")));
		
		
		// Check that comas and colons do not come through.
		assertFalse(filter.escape("one,two").contains(","));
		assertFalse(filter.escape("one:two").contains(":"));
		assertFalse(filter.escape("one%,two").contains(","));
		assertFalse(filter.escape("one&:two").contains(":"));
		assertFalse(filter.escape(",one,two,").contains(","));
		assertFalse(filter.escape(":one,two:").contains(":"));
	}
	
	/**
	 * Test the internal method to encode and decode lists.
	 */
	@Test
	public void testEncodeListAndDecodeList() {
		
		UriActiveSearchFilterImpl filter = Spring.getBeanOfType(UriActiveSearchFilterImpl.class);

		
		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		
		StringBuilder encoding = new StringBuilder();
		filter.encodeList(encoding, list);
		assertEquals("one,two:",encoding.toString());
		
		// Normally the split method removes the colon, so we'll just skip that step here since that's not what we're testing.
		
		List<String> result = filter.decodeList("one,two", String.class);
		assertEquals("one",result.get(0));
		assertEquals("two",result.get(1));
		assertEquals(2,result.size());
		
	}
	
}
