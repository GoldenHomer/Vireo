package controllers.settings;

import org.tdl.vireo.model.RoleType;

import play.mvc.With;

import controllers.Authentication;
import controllers.Security;
import controllers.SettingsTab;

@With(Authentication.class)
public class ApplicationSettingsTab extends SettingsTab {

	
	@Security(RoleType.MANAGER)
	public static void applicationSettings(){
		String nav = "settings";
		String subNav = "application";
		renderTemplate("SettingTabs/applicationSettings.html",nav, subNav);
	}
}
