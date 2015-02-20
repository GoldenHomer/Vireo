package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.email.SystemEmailTemplateService;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.jpa.JpaSettingsRepositoryImpl;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.security.AuthenticationMethod;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.spring.Spring;

public class FirstUser extends AbstractVireoController {
	
	public static SystemEmailTemplateService systemEmailService = Spring.getBeanOfType(SystemEmailTemplateService.class);
	public static Indexer indexer = Spring.getBeanOfType(Indexer.class);
	
	public static void createUser() {
		if(firstUser == null || firstUser == false)
				Application.index();
				
		if(params.get("createFirstUser")!=null) {
			//Get form parameters
			String firstName = params.get("firstName");
			String lastName = params.get("lastName");
			String email = params.get("email");
			String password1 = params.get("password1");
			String password2 = params.get("password2");
			String netid = params.get("netid");
			
			verify(firstName, lastName, email, password1, password2, netid);
		
			if(!validation.hasErrors()) {
				// Create the account.
				context.turnOffAuthorization();
				Person person = personRepo.createPerson(netid, email, firstName, lastName, RoleType.ADMINISTRATOR).save();
				person.setPassword(password1);
				person.save();
				context.turnOffAuthorization();
				context.login(person);
				
				// Notify all the authentication methods of the new user.
				List<AuthenticationMethod> methods = getEnabledAuthenticationMethods();
				for (AuthenticationMethod method : methods) 
					method.personCreated(request, person);
				
				// Log the person in
				session.put("personId", person.getId());
				session.put("firstName", person.getFirstName());
				session.put("lastName", person.getLastName());
				session.put("displayName", person.getDisplayName());
				
				//Generate System Email Templates
				systemEmailService.generateAllSystemEmailTemplates();
				
				// Setup Embargos
				if(!(settingRepo instanceof JpaSettingsRepositoryImpl))
					new InitializeEmbargos().doJob(settingRepo);
				
				// Setup default Committee Member Role Types
				for(String roleType : COMMITTEE_MEMBER_ROLE_TYPES_DEFINITIONS) {
					settingRepo.createCommitteeMemberRoleType(roleType, DegreeLevel.UNDERGRADUATE).save();
					settingRepo.createCommitteeMemberRoleType(roleType, DegreeLevel.MASTERS).save();
					settingRepo.createCommitteeMemberRoleType(roleType, DegreeLevel.DOCTORAL).save();
				}
				
				//Flag that any future user is not the first user.
				firstUser = false;
				
				// Go to the settings page
				try {
					SettingsTab.settingsRedirect();
				} finally {
					// Do a fresh rebuild of the index after the page has loaded.
					indexer.deleteAndRebuild(false);
				}
				
			} else {
				
				renderTemplate("FirstUser/createUser.html",
						firstName,
						lastName,
						email,
						password1,
						password2,
						netid
				);
			}
		}
		
		renderTemplate("FirstUser/createUser.html");
	}
	
	/**
	 * Verify that the new user information is correct.
	 * 
	 * @param firstName (The first name provided)
	 * @param lastName (The last name provided)
	 * @param email (The email provided)
	 * @param password1 (The password provided)
	 * @param password2 (Verification of password1)
	 * @param netid (The netid provided)
	 * @return
	 */
	public static boolean verify(String firstName, String lastName, String email, String password1, String password2, String netid){
		
		int numberOfErrorsBefore = validation.errors().size();
		
		if(firstName==null || firstName.isEmpty())
			validation.addError("firstName", "Please enter your first name.");
		
		if(lastName==null || lastName.isEmpty())
			validation.addError("lastName", "Please enter your last name.");
		
		if(email==null || email.isEmpty()) {
			validation.addError("email", "Please enter an email.");
		} else {
			try {
				new InternetAddress(email).validate();
			} catch (AddressException ae) {
				validation.addError("email", "The email provided is invalid.");
			}
		}
		
		if(password1==null || password1.isEmpty()) {
			validation.addError("password1", "Please enter a password.");
		
		} else if(password2==null || password2.isEmpty()) {
				validation.addError("password2", "Please verify the password.");
		
		} else if(!password2.equals(password1)) {
				validation.addError("passwords", "The passwords provided do not match.");
		}
				
		if(numberOfErrorsBefore == validation.errors().size())
			return false;
		else
			return true;
	}
	
	/**
	 * @return The list of all enabled authenticationMethods.
	 */
	private static List<AuthenticationMethod> getEnabledAuthenticationMethods() {
		Map<String,AuthenticationMethod> methodMap = Spring.getBeansOfType(AuthenticationMethod.class);
		List<AuthenticationMethod> enabledMethods = new ArrayList<AuthenticationMethod>();
		for(AuthenticationMethod method : methodMap.values()) {
			if (method.isEnabled())
				enabledMethods.add(method);
		}
		return enabledMethods;
	}
	
	/**
	 * Initial Committee Member Role Types
	 */
	
	private static final String[] COMMITTEE_MEMBER_ROLE_TYPES_DEFINITIONS = {"Chair"};
	
	/**
	 * Initial Embargo Types to create
	 */
	
	private static final EmbargoArray[] EMBARGO_DEFINTITIONS = {
		new EmbargoArray("None",
				"You do not desire to place any holds on your thesis.  It is ready to be published to the Digital Library right away.",
				0,
				true,
				true,
				EmbargoGuarantor.DEFAULT),
		new EmbargoArray("Journal Hold",
				"The work will be delayed for publication by one year because of a restriction from publication in an academic journal.", 
				12,
				true,
				true,
				EmbargoGuarantor.DEFAULT),
		new EmbargoArray("Patent Hold",
				"You request that we withhold your thesis from publication in the Digital Library for two years for proprietary purposes or for securing a patent.",
				24,
				true,
				true,
				EmbargoGuarantor.DEFAULT),
	    new EmbargoArray("Other Embargo Period",
	    		"The work will be delayed for publication by an indefinite amount of time.",
	    		null,
	    		false,
				true,
				EmbargoGuarantor.DEFAULT),
	    new EmbargoArray("None",
	    		"The work will be published after approval.",
	    		0,
	    		true,
				true,
	    		EmbargoGuarantor.PROQUEST),
	    new EmbargoArray("6-month Journal Hold",
				"The full text of this work will be held/restricted from worldwide access on the internet for six months from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication.", 
				6,
				true,
				true,
				EmbargoGuarantor.PROQUEST),
		new EmbargoArray("1-year Journal Hold",
				"The full text of this work will be held/restricted from worldwide access on the internet for one year from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication.", 
				12,
				true,
				true,
				EmbargoGuarantor.PROQUEST),
		new EmbargoArray("2-year Journal Hold",
				"The full text of this work will be held/restricted from worldwide access on the internet for two years from the semester/year of graduation to meet academic publisher restrictions or to allow time for publication.", 
				24,
				true,
				true,
				EmbargoGuarantor.PROQUEST),
		new EmbargoArray("Flexible/Delayed Release Embargo Period",
	    		"The work will be delayed for publication by an indefinite amount of time.",
	    		null,
	    		false,
				true,
	    		EmbargoGuarantor.PROQUEST)
	};
	
	private static class EmbargoArray {
		
		String name;
		String description;
		Integer duration;
		boolean active;
		boolean isSystem;
		EmbargoGuarantor guarantor;
		
		EmbargoArray(String name, String description, Integer duration, boolean active, boolean isSystem, EmbargoGuarantor guarantor) {
			this.name = name;
			this.description = description;
			this.duration = duration;
			this.active = active;
			this.isSystem = isSystem;
			this.guarantor = guarantor;
		}
		
		
	}
	
	@OnApplicationStart
	public static class InitializeEmbargos extends Job {
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		
		public void doJob(SettingsRepository settingRepo) {
			this.settingRepo = settingRepo;
			doJob();
		}
		
		public void doJob() {
			
			try {
				// turn off authorization if we're saving
				context.turnOffAuthorization();
				
				List<EmbargoType> embargoTypes = settingRepo.findAllEmbargoTypes();
				// Setup Embargos
				for(EmbargoArray embargoDefinition : EMBARGO_DEFINTITIONS) {
					boolean found = false;
					for(EmbargoType installedEmbargo : embargoTypes) {
						if( installedEmbargo.getName().equals(embargoDefinition.name) && installedEmbargo.getGuarantor().toString().equals(embargoDefinition.guarantor.toString())) {
							found = true;
							installedEmbargo.setDescription(embargoDefinition.description);
							installedEmbargo.setDuration(embargoDefinition.duration);
							installedEmbargo.setSystemRequired(embargoDefinition.isSystem);
							installedEmbargo.save();
						}
					}
					if(!found) {
						EmbargoType newembargo = settingRepo.createEmbargoType(embargoDefinition.name, embargoDefinition.description, embargoDefinition.duration, embargoDefinition.active, embargoDefinition.guarantor);
						newembargo.setSystemRequired(embargoDefinition.isSystem);
						newembargo.save();
					}
				}
			} catch (RuntimeException re) {
				Logger.error(re, "Unable to initialize default embargos.");
			} finally {
				// turn on authorization after we're done
				context.restoreAuthorization();
			}
		}
	}
}