package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.repo.FieldGlossRepo;
import org.tdl.vireo.model.repo.custom.FieldGlossRepoCustom;

public class FieldGlossRepoImpl implements FieldGlossRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private FieldGlossRepo fieldGlossRepo;
	
	@Override
	public FieldGloss create(String value) {
		return fieldGlossRepo.save(new FieldGloss(value));
	}
	
	@Override
	@Transactional
	public void delete(FieldGloss fieldGloss) {
		entityManager.remove(entityManager.contains(fieldGloss) ? fieldGloss : entityManager.merge(fieldGloss));
	}
	
}
