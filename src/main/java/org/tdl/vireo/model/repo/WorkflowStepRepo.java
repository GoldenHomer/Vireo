package org.tdl.vireo.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tdl.vireo.model.Workflow;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.custom.WorkflowStepRepoCustom;

public interface WorkflowStepRepo extends JpaRepository<WorkflowStep, Long>, WorkflowStepRepoCustom {

    public List<WorkflowStep> findByName(String name);
    
    public WorkflowStep findByNameAndWorkflow(String name, Workflow workflow);
    
}
