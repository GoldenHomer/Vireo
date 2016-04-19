package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    @Override
    public ControlledVocabulary create(String name, Language language) {
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary(name, language);
        controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
        return controlledVocabularyRepo.save(controlledVocabulary);
    }
    
    @Override
    public ControlledVocabulary create(String name, String entityName, Language language) {
        ControlledVocabulary controlledVocabulary = new ControlledVocabulary(name, entityName, language);
        controlledVocabulary.setPosition(controlledVocabularyRepo.count() + 1);
        return controlledVocabularyRepo.save(controlledVocabulary);
    }
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(ControlledVocabulary.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(ControlledVocabulary.class, column);
    }
    
    @Override
    public void remove(Long index) {
        orderedEntityService.remove(controlledVocabularyRepo, ControlledVocabulary.class, index);
    }

    @Override
    public ControlledVocabulary validateCreate(ControlledVocabulary controlledVocabulary) {
        if(controlledVocabularyRepo.findByName(controlledVocabulary.getName()) != null) {
            controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", controlledVocabulary.getName() + " is already a controlled vocabulary!"));
        }
        return controlledVocabulary;
    }
    
    @Override
    public ControlledVocabulary validateUpdate(ControlledVocabulary controlledVocabulary) {
        ControlledVocabulary controlledVocabularyToUpdate = null;
        // make sure we're receiving an Id from the front-end
        if (controlledVocabulary.getId() == null) {
            controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", "Cannot update a ControlledVocabulary without an id!"));
        }
        // we have an id
        else {
            controlledVocabularyToUpdate = controlledVocabularyRepo.findOne(controlledVocabulary.getId());
            ControlledVocabulary controlledVocabularyExistingName = controlledVocabularyRepo.findByName(controlledVocabulary.getName());

            // make sure we won't have any unique constraint violations
            if(controlledVocabularyExistingName != null) {
                controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", controlledVocabulary.getName() + " is already a controlled vocabulary!"));
            }
            
            // make sure we're updating an existing controlled vocabulary
            if(controlledVocabularyToUpdate == null) {
                controlledVocabulary.getBindingResult().addError(new ObjectError("controlledVocabulary", controlledVocabulary.getName() + " can't be updated, it doesn't exist!"));
            }
        }
        // if we have no errors, do the update!
        if(!controlledVocabulary.getBindingResult().hasErrors()){
            controlledVocabularyToUpdate.setName(controlledVocabulary.getName());
            controlledVocabularyToUpdate.setBindingResult(controlledVocabulary.getBindingResult());
            controlledVocabulary = controlledVocabularyToUpdate;
        }
        
        return controlledVocabulary;
    }
}
