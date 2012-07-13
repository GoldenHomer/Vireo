package controllers.settings;

import static org.tdl.vireo.model.Configuration.SUBMISSIONS_OPEN;

import java.text.DateFormatSymbols;
import java.util.List;

import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.RoleType;

import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class ConfigurableSettingsTab extends SettingsTab {

	/**
	 * Display the configurable settings page.
	 */
	@Security(RoleType.MANAGER)
	public static void configurableSettings() {
		
		List<College> colleges = settingRepo.findAllColleges();
		List<Department> departments = settingRepo.findAllDepartments();
		List<Major> majors = settingRepo.findAllMajors();
		List<Degree> degrees = settingRepo.findAllDegrees();
		List<DocumentType> docTypes = settingRepo.findAllDocumentTypes();
		List<GraduationMonth> gradMonths = settingRepo.findAllGraduationMonths();

		
		renderArgs.put("UNDERGRADUATE", DegreeLevel.UNDERGRADUATE);
		renderArgs.put("MASTERS", DegreeLevel.MASTERS);
		renderArgs.put("DOCTORAL", DegreeLevel.DOCTORAL);

		
		String nav = "settings";
		String subNav = "config";
		renderTemplate("SettingTabs/configurableSettings.html", nav, subNav, 
				
				// Sortable lists
				colleges, departments, majors, degrees, docTypes, gradMonths);
	}

	// ////////////////////////////////////////////
	// COLLEGE AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new College. The id of the new college will be returned.
	 * 
	 * @param name
	 *            The name of the new college
	 */
	@Security(RoleType.MANAGER)
	public static void addCollegeJSON(String name) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Add the new action to the end of the list.
			List<College> colleges = settingRepo.findAllColleges();

			College college = settingRepo.createCollege(name);
			colleges.add(college);

			saveModelOrder(colleges);

			name = escapeJavaScript(college.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + college.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Edit an existing college's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param collegeId
	 *            The id of the college to be edited, in the form "college_id"
	 * @param name
	 *            The new name
	 */
	@Security(RoleType.MANAGER)
	public static void editCollegeJSON(String collegeId, String name) {
		System.out.println("1");
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Save the new label
			String[] parts = collegeId.split("_");
			Long id = Long.valueOf(parts[1]);
			College college = settingRepo.findCollege(id);
			college.setName(name);
			college.save();

			name = escapeJavaScript(name);

			System.out.println("2");

			renderJSON("{ \"success\": \"true\", \"id\": " + college.getId() + ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			System.out.println("3");
			re.printStackTrace();
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing college
	 * 
	 * @param collegeId
	 *            The id of the college to be removed of the form "college_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeCollegeJSON(String collegeId) {
		try {
			// Delete the old college
			String[] parts = collegeId.split("_");
			Long id = Long.valueOf(parts[1]);
			College college = settingRepo.findCollege(id);
			college.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Reorder a list of colleges.
	 * 
	 * @param collegeIds
	 *            An ordered list of ids in the form:
	 *            "college_1,college_3,college_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderCollegesJSON(String collegeIds) {

		try {

			if (collegeIds != null && collegeIds.trim().length() > 0) {
				// Save the new order
				List<College> colleges = resolveIds(collegeIds, College.class);
				saveModelOrder(colleges);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	// ////////////////////////////////////////////
	// DEPARTMENT AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new Department. The id of the new department will be returned.
	 * 
	 * @param name
	 *            The name of the new department
	 */
	@Security(RoleType.MANAGER)
	public static void addDepartmentJSON(String name) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Add the new action to the end of the list.
			List<Department> departments = settingRepo.findAllDepartments();

			Department department = settingRepo.createDepartment(name);
			departments.add(department);

			saveModelOrder(departments);

			name = escapeJavaScript(department.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + department.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Edit an existing department's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param departmentId
	 *            The id of the department to be edited, in the form
	 *            "department_id"
	 * @param name
	 *            The new name
	 */
	@Security(RoleType.MANAGER)
	public static void editDepartmentJSON(String departmentId, String name) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Save the new label
			String[] parts = departmentId.split("_");
			Long id = Long.valueOf(parts[1]);
			Department department = settingRepo.findDepartment(id);
			department.setName(name);
			department.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + department.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Remove an existing department
	 * 
	 * @param departmentId
	 *            The id of the department to be removed of the form
	 *            "department_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeDepartmentJSON(String departmentId) {
		try {
			// Delete the old department
			String[] parts = departmentId.split("_");
			Long id = Long.valueOf(parts[1]);
			Department department = settingRepo.findDepartment(id);
			department.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Reorder a list of departments.
	 * 
	 * @param departmentIds
	 *            An ordered list of ids in the form:
	 *            "department_1,department_3,department_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDepartmentsJSON(String departmentIds) {

		try {

			if (departmentIds != null && departmentIds.trim().length() > 0) {
				// Save the new order
				List<Department> departments = resolveIds(departmentIds,
						Department.class);
				saveModelOrder(departments);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	// ////////////////////////////////////////////
	// MAJOR AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new Major. The id of the new major will be returned.
	 * 
	 * @param name
	 *            The name of the new major
	 */
	@Security(RoleType.MANAGER)
	public static void addMajorJSON(String name) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Add the new action to the end of the list.
			List<Major> majors = settingRepo.findAllMajors();

			Major major = settingRepo.createMajor(name);
			majors.add(major);

			saveModelOrder(majors);

			name = escapeJavaScript(major.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + major.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Edit an existing major's name. Both the id and new name will be returned.
	 * 
	 * @param majorId
	 *            The id of the major to be edited, in the form "major_id"
	 * @param name
	 *            The new name
	 */
	@Security(RoleType.MANAGER)
	public static void editMajorJSON(String majorId, String name) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			// Save the new label
			String[] parts = majorId.split("_");
			Long id = Long.valueOf(parts[1]);
			Major major = settingRepo.findMajor(id);
			major.setName(name);
			major.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + major.getId()
					+ ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Remove an existing major
	 * 
	 * @param majorId
	 *            The id of the major to be removed of the form "major_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeMajorJSON(String majorId) {
		try {
			// Delete the old major
			String[] parts = majorId.split("_");
			Long id = Long.valueOf(parts[1]);
			Major major = settingRepo.findMajor(id);
			major.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Reorder a list of majors.
	 * 
	 * @param majorIds
	 *            An ordered list of ids in the form: "major_1,major_3,major_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderMajorsJSON(String majorIds) {

		try {

			if (majorIds != null && majorIds.trim().length() > 0) {
				// Save the new order
				List<Major> majors = resolveIds(majorIds, Major.class);
				saveModelOrder(majors);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	// ////////////////////////////////////////////
	// DEGREE AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new Degree. The id of the new degree will be returned.
	 * 
	 * @param name
	 *            The name of the new degree
	 * @param level 
	 * 			  The level id for the new degree
	 */
	@Security(RoleType.MANAGER)
	public static void addDegreeJSON(String name, int level) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Add the new action to the end of the list.
			List<Degree> degrees = settingRepo.findAllDegrees();

			Degree degree = settingRepo.createDegree(name,degreeLevel);
			degrees.add(degree);

			saveModelOrder(degrees);

			name = escapeJavaScript(degree.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + degree.getId()	+ ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing degree's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param degreeId
	 *            The id of the degree to be edited, in the form "degree_id"
	 * @param name
	 *            The new name
	 * @param level
	 * 			  The new level id
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void editDegreeJSON(String degreeId, String name, int level) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Save the new label
			String[] parts = degreeId.split("_");
			Long id = Long.valueOf(parts[1]);
			Degree degree = settingRepo.findDegree(id);
			degree.setName(name);
			degree.setLevel(degreeLevel);
			degree.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + degree.getId() + ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing degree
	 * 
	 * @param degreeId
	 *            The id of the degree to be removed of the form "degree_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeDegreeJSON(String degreeId) {
		try {
			// Delete the old degree
			String[] parts = degreeId.split("_");
			Long id = Long.valueOf(parts[1]);
			Degree degree = settingRepo.findDegree(id);
			degree.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Reorder a list of degrees.
	 * 
	 * @param degreeIds
	 *            An ordered list of ids in the form:
	 *            "degree_1,degree_3,degree_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDegreesJSON(String degreeIds) {

		try {

			if (degreeIds != null && degreeIds.trim().length() > 0) {
				// Save the new order
				List<Degree> degrees = resolveIds(degreeIds, Degree.class);
				saveModelOrder(degrees);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	// ////////////////////////////////////////////
	// DOCUMENT TYPES AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new DocumentType. The id of the new docType will be returned.
	 * 
	 * @param name
	 *            The name of the new docType
	 * @param level 
	 * 			  The level id for the new documentType
	 */
	@Security(RoleType.MANAGER)
	public static void addDocumentTypeJSON(String name, int level) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Add the new action to the end of the list.
			List<DocumentType> documentTypes = settingRepo.findAllDocumentTypes();

			DocumentType documentType = settingRepo.createDocumentType(name,degreeLevel);
			documentTypes.add(documentType);

			saveModelOrder(documentTypes);

			name = escapeJavaScript(documentType.getName());

			renderJSON("{ \"success\": \"true\", \"id\": " + documentType.getId()	+ ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing documentType's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param documentTypeId
	 *            The id of the documentType to be edited, in the form "documentType_id"
	 * @param name
	 *            The new name
	 * @param level
	 * 			  The new level id
	 * 
	 */
	@Security(RoleType.MANAGER)
	public static void editDocumentTypeJSON(String documentTypeId, String name, int level) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			DegreeLevel degreeLevel = DegreeLevel.find(level);

			// Save the new label
			String[] parts = documentTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			DocumentType documentType = settingRepo.findDocumentType(id);
			documentType.setName(name);
			documentType.setLevel(degreeLevel);
			documentType.save();

			name = escapeJavaScript(name);

			renderJSON("{ \"success\": \"true\", \"id\": " + documentType.getId() + ", \"name\": \"" + name + "\", \"level\": "+degreeLevel.getId()+" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing documentType
	 * 
	 * @param documentTypeId
	 *            The id of the documentType to be removed of the form "documentType_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeDocumentTypeJSON(String documentTypeId) {
		try {
			// Delete the old documentType
			String[] parts = documentTypeId.split("_");
			Long id = Long.valueOf(parts[1]);
			DocumentType documentType = settingRepo.findDocumentType(id);
			documentType.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message
					+ "\" }");
		}
	}

	/**
	 * Reorder a list of documentTypes.
	 * 
	 * @param documentTypeIds
	 *            An ordered list of ids in the form:
	 *            "documentType_1,documentType_3,documentType_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderDocumentTypesJSON(String documentTypeIds) {

		try {

			if (documentTypeIds != null && documentTypeIds.trim().length() > 0) {
				// Save the new order
				List<DocumentType> documentTypes = resolveIds(documentTypeIds, DocumentType.class);
				saveModelOrder(documentTypes);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}


	// ////////////////////////////////////////////
	// GRADUATION MONTH AJAX
	// ////////////////////////////////////////////

	/**
	 * Create a new GraduationMonth. The id of the new month will be returned.
	 * 
	 * @param name
	 *            The name of the new graduation month
	 */
	@Security(RoleType.MANAGER)
	public static void addGraduationMonthJSON(String name) {

		try {
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Name is required");

			int monthInt = monthNameToInt(name);
			
			// Add the new action to the end of the list.
			List<GraduationMonth> months = settingRepo.findAllGraduationMonths();

			GraduationMonth month = settingRepo.createGraduationMonth(monthInt);
			months.add(month);

			saveModelOrder(months);

			name = escapeJavaScript(month.getMonthName());

			renderJSON("{ \"success\": \"true\", \"id\": " + month.getId() + ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Edit an existing month's name. Both the id and new name will be
	 * returned.
	 * 
	 * @param graduationMonthId
	 *            The id of the month to be edited, in the form "graduationMonth_id"
	 * @param name
	 *            The new name of the month
	 */
	@Security(RoleType.MANAGER)
	public static void editGraduationMonthJSON(String graduationMonthId, String name) {
		try {
			// Check input
			if (name == null || name.trim().length() == 0)
				throw new IllegalArgumentException("Month name is required");

			int monthInt = monthNameToInt(name);
			
			// Save the new label
			String[] parts = graduationMonthId.split("_");
			Long id = Long.valueOf(parts[1]);
			GraduationMonth month = settingRepo.findGraduationMonth(id);
			
			month.setMonth(monthInt);
			month.save();

			name = escapeJavaScript(month.getMonthName());

			renderJSON("{ \"success\": \"true\", \"id\": " + month.getId() + ", \"name\": \"" + name + "\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Remove an existing graduation month
	 * 
	 * @param graduationMonthId
	 *            The id of the graduation month to be removed of the form "graduationMonth_id"
	 */
	@Security(RoleType.MANAGER)
	public static void removeGraduationMonthJSON(String graduationMonthId) {
		try {
			// Delete the old college
			String[] parts = graduationMonthId.split("_");
			Long id = Long.valueOf(parts[1]);
			GraduationMonth month = settingRepo.findGraduationMonth(id);
			month.delete();

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}

	/**
	 * Reorder a list of graduation months.
	 * 
	 * @param graduationMonthIds
	 *            An ordered list of ids in the form:
	 *            "graduationMonth_1,graduationMonth_3,graduationMonth_2"
	 */
	@Security(RoleType.MANAGER)
	public static void reorderGraduationMonthsJSON(String graduationMonthIds) {

		try {

			if (graduationMonthIds != null && graduationMonthIds.trim().length() > 0) {
				// Save the new order
				List<GraduationMonth> months = resolveIds(graduationMonthIds, GraduationMonth.class);
				saveModelOrder(months);
			}

			renderJSON("{ \"success\": \"true\" }");
		} catch (RuntimeException re) {
			String message = escapeJavaScript(re.getMessage());
			renderJSON("{ \"failure\": \"true\", \"message\": \"" + message + "\" }");
		}
	}
	
	
	
	
	/**
	 * Internal method to translate the name of a month into it's integer value.
	 * 
	 * If the month name is invalid then a user displayable message is thrown as
	 * an IllegalArgumentException.
	 * 
	 * @param monthName
	 *            The name of a month in the default lanugage.
	 * @return The integer value of the month, january=0, december=11.
	 */
	protected static int monthNameToInt(String monthName) {
		
		if (monthName == null || monthName.trim().length() == 0)
			throw new IllegalArgumentException("monthName is required.");
		
		monthName = monthName.toLowerCase();
		
		String[] months = new DateFormatSymbols().getMonths();
		for (int i =0; i <months.length; i++) {
			
			if (monthName.equalsIgnoreCase(months[i]))
				return i;
		}
		
		throw new IllegalArgumentException("The month '"+monthName+"' is invalid, month names should be spelled out completely such as \"January\", \"Feburary\", etc...");
	}

}