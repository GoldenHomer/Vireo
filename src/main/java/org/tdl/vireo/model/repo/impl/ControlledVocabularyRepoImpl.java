package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.repo.ControlledVocabularyRepo;
import org.tdl.vireo.model.repo.custom.ControlledVocabularyRepoCustom;

public class ControlledVocabularyRepoImpl implements ControlledVocabularyRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	@Override
	public ControlledVocabulary create(String name) {
		return controlledVocabularyRepo.save(new ControlledVocabulary(name));
	}
	
	@Override
	@Transactional
	public void delete(ControlledVocabulary controlledVocabulary) {
		entityManager.remove(entityManager.contains(controlledVocabulary) ? controlledVocabulary : entityManager.merge(controlledVocabulary));
	}
	
}
