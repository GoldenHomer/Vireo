package org.tdl.vireo.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.WorkflowEmailRule;

import play.modules.spring.Spring;

@Entity
@Table(name = "email_workflow_rule_condition")
public class JpaEmailWorkflowRuleConditionImpl extends JpaAbstractModel<JpaEmailWorkflowRuleConditionImpl> implements AbstractWorkflowRuleCondition {

	@Column(nullable = false)
	public int displayOrder;

	@Column
	public Long conditionId;
	
	@OneToOne(mappedBy = "condition")
    private JpaWorkflowEmailRuleImpl ruleId;
	
	@Enumerated
	public ConditionType conditionType;

	@Override
	public JpaEmailWorkflowRuleConditionImpl save() {
		assertManager();

		return super.save();
	}

	@Override
	public JpaEmailWorkflowRuleConditionImpl delete() {
		assertManager();

		return super.delete();
	}

	@Override
	public int getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(int displayOrder) {

		assertManager();
		this.displayOrder = displayOrder;
	}

	@Override
	public Long getConditionId() {
		return this.conditionId;
	}

	@Override
	public void setConditionId(Long id) {
		this.conditionId = id;
	}

	@Override
	public ConditionType getConditionType() {
		return this.conditionType;
	}

	@Override
	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}


	@Override
	public String getConditionIdDisplayName() {
		String displayName = "none";
		
		if(this.conditionId == null || this.conditionType == null) {
			return displayName;
		}
		
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		
		switch(this.conditionType) {
			case College:
				displayName = settingRepo.findCollege(this.conditionId).getName();
				break;
			case Department:
				displayName = settingRepo.findDepartment(this.conditionId).getName();
				break;
			case Program:
				displayName = settingRepo.findProgram(this.conditionId).getName();
				break;
			case Always:
			default:
				displayName = "none";
				break;
		}
		
		return displayName;
	}
}
