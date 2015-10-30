package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Override
    public FieldProfile create(FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean required) {
        return fieldProfileRepo.save(new FieldProfile(fieldPredicate, inputType, repeatable, required));
    }

}
