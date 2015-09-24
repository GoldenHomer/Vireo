package org.tdl.vireo.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.repo.WorkflowRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.WorkflowRepoCustom;

public class WorkflowRepoImpl implements WorkflowRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private WorkflowRepo workflowRepo;
	
	@Autowired
	private WorkflowStepRepo workflowStepRepo;
	
	@Override
	public Workflow create(String name, Boolean inheritable) {
		return workflowRepo.save(new Workflow(name, inheritable));
	}
		
	@Override
	@Transactional
	public void delete(Workflow workflow) {
		entityManager.remove(entityManager.contains(workflow) ? workflow : entityManager.merge(workflow));
	}
	
}
