package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "submitter_id", "state_id" }) )
public class Submission extends BaseEntity {
    
    @OneToOne(optional = false)
    private User submitter;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private SubmissionState state;

    @ManyToMany(cascade = { DETACH, REFRESH, MERGE }, fetch = EAGER)
    private Set<Organization> organizations;

    @OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private Set<FieldValue> fieldValues;

    @OneToMany(cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private Set<WorkflowStep> submissionWorkflowSteps;
    
    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar dateOfGraduation;

    public Submission() {
        setOrganizations(new TreeSet<Organization>());
        setFieldValues(new TreeSet<FieldValue>());
        setSubmissionWorkflowSteps(new TreeSet<WorkflowStep>());
    }

    /**
     * @param submitter
     * @param state
     */
    public Submission(User submitter, SubmissionState state) {
        this();
        setSubmitter(submitter);
        setState(state);
    }
    
    /**
     * 
     * @return the submitter
     */
    public User getSubmitter() {
        return submitter;
    }
    
    /**
     * 
     * @param submitter
     */
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    /**
     * @return the state
     */
    public SubmissionState getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(SubmissionState state) {
        this.state = state;
    }

    /**
     * @return the organizations
     */
    public Set<Organization> getOrganizations() {
        return organizations;
    }

    /**
     * @param organizations
     *            the organizations to set
     */
    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }

    /**
     * 
     * @param organization
     */
    public void addOrganization(Organization organization) {
        getOrganizations().add(organization);
    }

    /**
     * 
     * @param organization
     */
    public void removeOrganization(Organization organization) {
        getOrganizations().remove(organization);
    }

    /**
     * @return the fieldvalues
     */
    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    /**
     * @param fieldvalues
     *            the fieldvalues to set
     */
    public void setFieldValues(Set<FieldValue> fieldvalues) {
        this.fieldValues = fieldvalues;
    }

    /**
     * 
     * @param fieldValue
     */
    public void addFieldValue(FieldValue fieldValue) {
        getFieldValues().add(fieldValue);
    }

    /**
     * 
     * @param fieldValue
     */
    public void removeFieldValue(FieldValue fieldValue) {
        getFieldValues().remove(fieldValue);
    }

    /**
     * @return the submissionWorkflowSteps
     */
    public Set<WorkflowStep> getSubmissionWorkflowSteps() {
        return submissionWorkflowSteps;
    }

    /**
     * @param submissionWorkflowSteps
     *            the submissionWorkflowSteps to set
     */
    public void setSubmissionWorkflowSteps(Set<WorkflowStep> submissionWorkflowSteps) {
        this.submissionWorkflowSteps = submissionWorkflowSteps;
    }

    /**
     * 
     * @param submissionWorkflowStep
     */
    public void addSubmissionWorkflowStep(WorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().add(submissionWorkflowStep);
    }

    /**
     * 
     * @param submissionWorkflowStep
     */
    public void removeSubmissionWorkflowStep(WorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().remove(submissionWorkflowStep);
    }

    /**
     * @return the dateOfGraduation
     */
    public Calendar getDateOfGraduation() {
        return dateOfGraduation;
    }

    /**
     * @param dateOfGraduation the dateOfGraduation to set
     */
    public void setDateOfGraduation(Calendar dateOfGraduation) {
        this.dateOfGraduation = dateOfGraduation;
    }
}
