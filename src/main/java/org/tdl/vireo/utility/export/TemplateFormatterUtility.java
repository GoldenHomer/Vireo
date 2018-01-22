package org.tdl.vireo.utility.export;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tdl.vireo.model.export.enums.GeneralKey;
import org.tdl.vireo.model.export.enums.DSpaceMETSKey;
import org.tdl.vireo.model.Submission;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class TemplateFormatterUtility {

	@Autowired
    private SpringTemplateEngine templateEngine;
	
	public String renderManifest(String packagerName, Submission submission) {
		Context context = new Context(Locale.getDefault());
		populateContext(context, submission);
		return templateEngine.process(packagerName, context);
	}

	private void populateContext(Context context, Submission submission) {
		for (GeneralKey key : GeneralKey.values()) {
			context.setVariable(key.name(), key.getValue(key, submission));
		}
		for (DSpaceMETSKey key : DSpaceMETSKey.values()) {
			context.setVariable(key.name(), key.getValue(key, submission));
		}
	}
}
