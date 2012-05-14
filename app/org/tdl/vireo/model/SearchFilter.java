package org.tdl.vireo.model;

import java.util.Date;
import java.util.List;

/**
 * A filter search is a set of parameters to search for a set of Vireo
 * submission. The object is used by the SubmissionRepository to filter the set
 * of all submissions by particular criteria.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface SearchFilter extends AbstractModel {

	/**
	 * @return The person who created this filter search
	 */
	public Person getCreator();

	/**
	 * @return The user assigned name of this filter search.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this filter search.
	 */
	public void setName(String name);

	/**
	 * @return Weather this filter search is publicly viewable by all vireo
	 *         reviewers.
	 */
	public boolean isPublic();

	/**
	 * @param publicFlag
	 *            Set this filter search as public or not.
	 */
	public void setPublic(boolean publicFlag);

	/**
	 * @return The list of all free-form search parameters
	 */
	public List<String> getSearchText();

	/**
	 * @param text
	 *            The new text search to add.
	 */
	public void addSearchText(String text);

	/**
	 * @param text
	 *            The text search to remove
	 */
	public void removeSearchText(String text);

	/**
	 * @return The list of all submission states to search for.
	 */
	public List<String> getStatus();

	/**
	 * @param status
	 *            The new status to add to the filter.
	 */
	public void addStatus(String status);

	/**
	 * @param status
	 *            Remove the status from the filter.
	 */
	public void removeStatus(String status);

	/**
	 * @return The list of assignees to filter for.
	 */
	public List<Person> getAssigned();

	/**
	 * @param assignee
	 *            A new assignee to add to the filter.
	 */
	public void addAssignee(Person assignee);

	/**
	 * @param assignee
	 *            The assignee to remove from the filter.
	 */
	public void removeAssignee(Person assignee);

	/**
	 * @return The list of graduation years
	 */
	public List<Integer> getGraduationYears();

	/**
	 * @param year
	 *            Add a year to the filter.
	 */
	public void addGraduationYear(Integer year);

	/**
	 * @param year
	 *            Remove a year from the filter.
	 */
	public void removeGraduationYear(Integer year);

	/**
	 * @return The list of graduation months
	 */
	public List<GraduationMonth> getGraduationMonths();

	/**
	 * @param month
	 *            Add a new graduation month to the filter
	 */
	public void addGraduationMonth(GraduationMonth month);

	/**
	 * @param month
	 *            Remove a graduation month from the filter.
	 */
	public void removeGraduationMonth(GraduationMonth month);

	/**
	 * @return The list of degrees
	 */
	public List<Degree> getDegrees();

	/**
	 * @param degree
	 *            A a new degree to the filter.
	 */
	public void addDegree(Degree degree);

	/**
	 * @param degree
	 *            Remove a degree from the filter.
	 */
	public void removeDegree(Degree degree);

	/**
	 * @return The list of departments.
	 */
	public List<Department> getDepartment();

	/**
	 * @param department
	 *            Add a new department to the filter.
	 */
	public void addDepartment(Department department);

	/**
	 * @param department
	 *            remove a department from the filter.
	 */
	public void removeDepartment(Department department);

	/**
	 * @return The list of colleges
	 */
	public List<College> getColleges();

	/**
	 * 
	 * @param college
	 *            Add a new college to the filter.
	 */
	public void addCollege(College college);

	/**
	 * 
	 * @param college
	 *            Remove a college from the filter.
	 */
	public void removeCollege(College college);

	/**
	 * 
	 * @return The list of majors
	 */
	public List<Major> getMajors();

	/**
	 * @param major
	 *            Add a new major to the filter.
	 */
	public void addMajor(Major major);

	/**
	 * @param major
	 *            Remove a major from the filter.
	 */
	public void removeMajor(Major major);

	/**
	 * @return The list of document types
	 */
	public List<DocumentType> getDocumentTypes();

	/**
	 * 
	 * @param documentType
	 *            add a new documentType to the filter.
	 */
	public void addDocumentType(DocumentType documentType);

	/**
	 * @param documentType
	 *            Remove a document type from the filter.
	 */
	public void removeDocumentType(DocumentType documentType);

	/**
	 * True -> applications are set to be released to UMI False -> applications
	 * are not set to be released. Null -> Either released or not.
	 * 
	 * @return How to filter for UMI release
	 */
	public Boolean getUMIRelease();

	/**
	 * @param value
	 *            Set the UMI release filter.
	 */
	public void setUMIRelease(Boolean value);

	/**
	 * @return The start of the current date range search.
	 */
	public Date getDateRangeStart();

	/**
	 * @return The end of the current date range search.
	 */
	public Date getDateRangeEnd();

	/**
	 * Set a new start and end date for a date range search.
	 * 
	 * @param start
	 *            The start date, inclusive
	 * @param end
	 *            The end date, inclusive.
	 */
	public void setDateRange(Date start, Date end);

}
