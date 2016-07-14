package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tdl.vireo.model.validation.WorkflowStepValidator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "originating_organization_id" }) )
@DiscriminatorValue("Org")
public class WorkflowStep extends AbstractWorkflowStep<WorkflowStep, FieldProfile, Note> {
   
    @ManyToOne(cascade = { REFRESH, MERGE }, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Organization.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    protected Organization originatingOrganization;
    
    @ManyToOne(cascade = { REFRESH, MERGE }, optional = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = WorkflowStep.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private WorkflowStep originatingWorkflowStep;
   
    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = FieldProfile.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<FieldProfile> originalFieldProfiles;
    
    @OneToMany(cascade = { REFRESH, MERGE }, fetch = EAGER, mappedBy = "originatingWorkflowStep")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Note.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<Note> originalNotes;

    public WorkflowStep() {
        setModelValidator(new WorkflowStepValidator());
    	setAggregateFieldProfiles(new ArrayList<FieldProfile>());
        setOriginalFieldProfiles(new ArrayList<FieldProfile>());
        setAggregateNotes(new ArrayList<Note>());
        setOriginalNotes(new ArrayList<Note>());
    }

    public WorkflowStep(String name) {
        this();
        setName(name);
        setOverrideable(true);
    }
    
    public WorkflowStep(String name, Organization originatingOrganization) {
        this(name);
        setOriginatingOrganization(originatingOrganization);
    }

    /**
     * @return the originatingWorkflowStep
     */
    public WorkflowStep getOriginatingWorkflowStep() {
        return originatingWorkflowStep;
    }

    /**
     * @param originatingWorkflowStep the originatingWorkflowStep to set
     */
    public void setOriginatingWorkflowStep(WorkflowStep originatingWorkflowStep) {
        this.originatingWorkflowStep = originatingWorkflowStep;
    }
    
    /**
     * @return the originatingOrganization
     */
    @Override
    public Organization getOriginatingOrganization() {
        return originatingOrganization;
    }

    /**
     * @param originatingOrganization the originatingOrganization to set
     */
    @Override
    public void setOriginatingOrganization(Organization originatingOrganization) {
        this.originatingOrganization = originatingOrganization;
    }
    
    /**
     * 
     * @return
     */
    public List<FieldProfile> getOriginalFieldProfiles() {
        return originalFieldProfiles;
    }

    /**
     * 
     * @param param
     */
    public void setOriginalFieldProfiles(List<FieldProfile> originalFieldProfiles) {
        this.originalFieldProfiles = originalFieldProfiles;
    }

    /**
     * 
     * @param fieldProfile
     */
    public void addOriginalFieldProfile(FieldProfile originalFieldProfile) {
        if(!getOriginalFieldProfiles().contains(originalFieldProfile)) {
            getOriginalFieldProfiles().add(originalFieldProfile);
        }
    	addAggregateFieldProfile(originalFieldProfile);
    }

    /**
     * 
     * @param fieldProfile
     */
    public void removeOriginalFieldProfile(FieldProfile originalFieldProfile) {
    	getOriginalFieldProfiles().remove(originalFieldProfile);
    	removeAggregateFieldProfile(originalFieldProfile);
    }
    
    /**
     * 
     * @param fp1
     * @param fp2
     * @return
     */
    public boolean replaceOriginalFieldProfile(FieldProfile fp1, FieldProfile fp2) {
        boolean res = false;
        int pos = 0;
        for(FieldProfile fp : getOriginalFieldProfiles()) {
            if(fp.getId().equals(fp1.getId())) {
                getOriginalFieldProfiles().remove(fp1);
                getOriginalFieldProfiles().add(pos, fp2);
                res = true;
                break;
            }
            pos++;
        }
        replaceAggregateFieldProfile(fp1, fp2);
        return res;
    }
    
    

    
    /**
     * 
     * @param fieldPredicate
     * @return
     */
    public FieldProfile getFieldProfileByPredicate(FieldPredicate fieldPredicate) {
        for (FieldProfile fieldProfile : getOriginalFieldProfiles()) {
            if (fieldProfile.getPredicate().equals(fieldPredicate))
                return fieldProfile;
        }
        return null;
    }

    public List<Note> getOriginalNotes() {
        return originalNotes;
    }

    public void setOriginalNotes(List<Note> originalNotes) {
        this.originalNotes = originalNotes;
    }

    public void addOriginalNote(Note originalNote) {
        if(!getOriginalNotes().contains(originalNote)) {
            getOriginalNotes().add(originalNote);
        }
        addAggregateNote(originalNote);
    }

    public void removeOriginalNote(Note originalNote) {
        getOriginalNotes().remove(originalNote);
        removeAggregateNote(originalNote);
    }
   
    public boolean replaceOriginalNote(Note n1, Note n2) {
        boolean res = false;
        int pos = 0;
        for(Note n : getOriginalNotes()) {
            if(n.getId().equals(n1.getId())) {
                getOriginalNotes().remove(n1);
                getOriginalNotes().add(pos, n2);
                res = true;
                break;
            }
            pos++;
        }
        replaceAggregateNote(n1, n2);
        return res;
    }

}
