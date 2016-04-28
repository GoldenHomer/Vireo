package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.repo.DocumentTypesRepo;
import org.tdl.vireo.model.repo.custom.DocumentTypesRepoCustom;
import org.tdl.vireo.service.OrderedEntityService;

public class DocumentTypesRepoImpl implements DocumentTypesRepoCustom {

    @Autowired
    private OrderedEntityService orderedEntityService;
    
    @Autowired
    private DocumentTypesRepo documentTypesRepo;
    
    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(DocumentType.class, src, dest);
    }
    
    @Override
    public void sort(String column) {
        orderedEntityService.sort(DocumentType.class, column);
    }
    
    @Override
    public void remove(Long index) {
        orderedEntityService.remove(documentTypesRepo, DocumentType.class, index);
    }

    @Override
    public DocumentType create(String name, DegreeLevel degreeLevel) {
        DocumentType documentType = new DocumentType(name, degreeLevel);
        documentType.setPosition(documentTypesRepo.count() + 1);
        return documentTypesRepo.save(documentType);
    }
    
}
