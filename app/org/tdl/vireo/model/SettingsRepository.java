package org.tdl.vireo.model;

import java.util.List;
import java.util.Set;

/**
 * The Vireo persistent repository for application-level settings. This object
 * follows the spring repository pattern, where this is the source for creating
 * and locating all persistent model objects. It is intended that this object
 * will be injected into all other spring beans that need access application
 * settings.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface SettingsRepository {

	// ///////////////////////////////////////////////
	// Degree, Major, College, and Department Models
	// ///////////////////////////////////////////////

	/**
	 * Create a new Degree object
	 * 
	 * @param name
	 *            Name of the degree.
	 * @param level
	 *            Level of the degree.
	 * @return A new degree object.
	 */
	public Degree createDegree(String name, DegreeLevel level);

	/**
	 * Find degree by unique id.
	 * 
	 * @param id
	 *            The degree's id.
	 * @return The degree object or null if not found.
	 */
	public Degree findDegree(Long id);

	/**
	 * Find all degree objects in order.
	 * 
	 * @return A list of all degrees, or an empty list if there are none.
	 */
	public List<Degree> findAllDegrees();

	/**
	 * Create a new Major object.
	 * 
	 * @param name
	 *            The name of the major.
	 * @return A new major object.
	 */
	public Major createMajor(String name);

	/**
	 * Find major by unique id.
	 * 
	 * @param id
	 *            The major's id.
	 * @return The major object or null if not found.
	 */
	public Major findMajor(Long id);

	/**
	 * Find all majors objects in order.
	 * 
	 * @return A list of all majors, or an empty list if there are none.
	 */
	public List<Major> findAllMajors();

	/**
	 * Create a new college object.
	 * 
	 * @param name
	 *            The name of the college.
	 * @return A new college object.
	 */
	public College createCollege(String name);

	/**
	 * Find a college by unique id.
	 * 
	 * @param id
	 *            The college's id.
	 * @return The college object or null if not found.
	 */
	public College findCollege(Long id);

	/**
	 * Find all college objects in order.
	 * 
	 * @return A list of all colleges, or an empty list if there are none.
	 */
	public List<College> findAllColleges();

	/**
	 * Create a new department object.
	 * 
	 * @param name
	 *            The name of the department.
	 * @return A new department object.
	 */
	public Department createDepartment(String name);

	/**
	 * Find department by unique id
	 * 
	 * @param id
	 *            The department's id.
	 * @return The department object or null if not found.
	 */
	public Department findDepartment(Long id);

	/**
	 * Find all department objects in order.
	 * 
	 * @return A list of all departments, or an empty list if there are none.
	 */
	public List<Department> findAllDepartments();

	// /////////////////////
	// Document Type Model
	// /////////////////////

	/**
	 * Create a new document type object.
	 * 
	 * @param name
	 *            The name of the document type.
	 * @param level
	 *            The level this document type is applicable for.
	 * @return A new document type object.
	 */
	public DocumentType createDocumentType(String name, DegreeLevel level);

	/**
	 * Find a document type by id.
	 * 
	 * @param id
	 *            The document type's id.
	 * @return The document type object, or null if not found.
	 */
	public DocumentType findDocumentType(Long id);

	/**
	 * Find all document type objects in order that are valid for a particular
	 * degree level.
	 * 
	 * @param level
	 *            The degree level to find.
	 * @return A list of all document types, or an empty list if there are none.
	 */
	public List<DocumentType> findAllDocumentTypes(DegreeLevel level);

	/**
	 * Find all document type objects in order.
	 * 
	 * @return A list of all document types, or an empty list if there are none.
	 */
	public List<DocumentType> findAllDocumentTypes();

	// ////////////////////
	// Embargo Type Model
	// ////////////////////

	/**
	 * Create a new embargo type.
	 * 
	 * @param active
	 *            Weather the embargo is currently active.
	 * @param description
	 *            A description of the embargo.
	 * @param duration
	 *            How long the embargo should last, or -1 for indeterminate.
	 * @return A new embargo type.
	 */
	public EmbargoType createEmbargoType(boolean active, String description,
			Long duration);

	/**
	 * Find embargo type by id.
	 * 
	 * @param id
	 *            The embargo type's id.
	 * @return The embargo type, or null if not found.
	 */
	public EmbargoType findEmbargoType(Long id);

	/**
	 * Find all embargo types objects in order.
	 * 
	 * @return A list of all embargo types, or an empty list if there are none.
	 */
	public List<EmbargoType> findAllEmbargoTypes();

	// ////////////////////////
	// Graduation Month Model
	// ////////////////////////

	/**
	 * Create a new graduation month.
	 * 
	 * @param month
	 *            The graduation month.
	 * @return A new graduation month object.
	 */
	public GraduationMonth createGraduationMonth(int month);

	/**
	 * Find a graduation month by id.
	 * 
	 * @param id
	 *            The month's id.
	 * @return The graduation month object, or null if not found.
	 */
	public GraduationMonth findGraduationMonth(Long id);

	/**
	 * Find all graduation months in order.
	 * 
	 * @return A list of all graduation months, or an empty list if there are
	 *         none.
	 */
	public List<GraduationMonth> findAllGraduationMonths();

	// //////////////////////
	// Email Template Model
	// //////////////////////

	/**
	 * Create a new email template
	 * 
	 * @param subject
	 *            The template's subject
	 * @param message
	 *            The template's messages
	 * @return A new email template object.
	 */
	public EmailTemplate createEmailTemplate(String subject, String message);

	/**
	 * Find an email template by id.
	 * 
	 * @param id
	 *            The email template's id.
	 * @return The email template object, or null if not found.
	 */
	public EmailTemplate findEmailTemplate(Long id);

	/**
	 * Find all email templates in order.
	 * 
	 * @return A list of all email templates, or an empty list if there are
	 *         none.
	 */
	public List<EmailTemplate> findAllEmailTemplates();

	// ///////////////////////////
	// Custom action definitions
	// ///////////////////////////

	/**
	 * Create a new custom action definition
	 * 
	 * @param label
	 *            The action's label
	 * @return A new custom action definition.
	 */
	public CustomActionDefinition createCustomActionDefinition(String label);

	/**
	 * Find custom action definition by id.
	 * 
	 * @param id
	 *            The action's id.
	 * @return The custom action definition, or null if not found.
	 */
	public CustomActionDefinition findCustomActionDefinition(Long id);

	/**
	 * Find all custom action definitions in order.
	 * 
	 * @return A list of all custom action definitions, or an empty list if
	 *         there are none.
	 */
	public List<CustomActionDefinition> findAllCustomActionDefinition();

	// ///////////////////////////
	// System wide configuration
	// ///////////////////////////

	/**
	 * Create a new system wide configuration.
	 * 
	 * @param name
	 *            The name of the canfiguration parameter.
	 * @param value
	 *            The value.
	 * @return A new configuration object.
	 */
	public Configuration createConfiguration(String name, String value);

	/**
	 * Find a system wide configuration object by id.
	 * 
	 * @param id
	 *            The configuration's id.
	 * @return The configuration object, or null if not found.
	 */
	public Configuration findConfiguration(Long id);

	/**
	 * Find a system wide configuration object by name.
	 * 
	 * @param name
	 *            The configuration's name.
	 * @return The configuration object, or null if not found.
	 */
	public Configuration findConfigurationByName(String name);

	/**
	 * Find all system wide configuration parameters.
	 * 
	 * @return A list of all system wide configuration parameters, or an empty
	 *         list if there are none.
	 */
	public List<Configuration> findAllConfigurations();

}
