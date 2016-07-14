package org.tdl.vireo.model.validation;

import edu.tamu.framework.enums.InputValidationType;
import edu.tamu.framework.validation.BaseModelValidator;
import edu.tamu.framework.validation.InputValidator;

public class ControlledVocabularyValidator extends BaseModelValidator {
    
    public ControlledVocabularyValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Controlled Vocabulary requires a name", nameProperty, true));
        this.addInputValidator(new InputValidator(InputValidationType.minLength, "Controlled Vocabulary name must be at least 2 characters", nameProperty, 2));
        this.addInputValidator(new InputValidator(InputValidationType.maxLength, "Controlled Vocabulary name cannot be more than 50 characters", nameProperty, 50));
        
        String languageProperty = "language";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Controlled Vocabulary requires a language", languageProperty, true));
        
        String isEntityPropertyProperty = "isEntityProperty";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Controlled Vocabulary requires an entity property flag", isEntityPropertyProperty, true));
        
        String isEnumProperty = "isEnum";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Controlled Vocabulary requires an enum property flag", isEnumProperty, true));
    }
    
}