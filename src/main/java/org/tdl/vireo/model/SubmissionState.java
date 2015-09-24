package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class SubmissionState extends BaseEntity {
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String name;
	 
	@Column(nullable = false)
	private Boolean archived;
	
	@Column(nullable = false)
	private Boolean publishable;
	
	@Column(nullable = false)
	private Boolean deletable;
	
	@Column(nullable = false)
	private Boolean editableByReviewer;
	
	@Column(nullable = false)
	private Boolean editableByStudent;
	
	@Column(nullable = false)
	private Boolean active;
	
	@ManyToOne(targetEntity = org.tdl.vireo.model.SubmissionState.class, cascade = { DETACH, REFRESH, MERGE }, optional = true)
	private Set<SubmissionState> transitionSubmissionStates;

	public SubmissionState() {
		setTransitionSubmissionStates(new HashSet<SubmissionState>());
	}
	
	/**
	 * @param name
	 * @param archived
	 * @param publishable
	 * @param deletable
	 * @param editableByReviewer
	 * @param editableByStudent
	 * @param active
	 */
	public SubmissionState(String name, Boolean archived, Boolean publishable, Boolean deletable, Boolean editableByReviewer, Boolean editableByStudent, Boolean active) {
		this();
		setName(name);
		setArchived(archived);
		setPublishable(publishable);
		setDeletable(deletable);
		setEditableByReviewer(editableByReviewer);
		setEditableByStudent(editableByStudent);
		setActive(active);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the archived
	 */
	public Boolean getArchived() {
		return archived;
	}

	/**
	 * @param archived
	 *            the archived to set
	 */
	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	/**
	 * @return the publishable
	 */
	public Boolean getPublishable() {
		return publishable;
	}

	/**
	 * @param publishable
	 *            the publishable to set
	 */
	public void setPublishable(Boolean publishable) {
		this.publishable = publishable;
	}

	/**
	 * @return the deletable
	 */
	public Boolean getDeletable() {
		return deletable;
	}

	/**
	 * @param deletable
	 *            the deletable to set
	 */
	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}

	/**
	 * @return the editableByReviewer
	 */
	public Boolean getEditableByReviewer() {
		return editableByReviewer;
	}

	/**
	 * @param editableByReviewer
	 *            the editableByReviewer to set
	 */
	public void setEditableByReviewer(Boolean editableByReviewer) {
		this.editableByReviewer = editableByReviewer;
	}

	/**
	 * @return the editableByStudent
	 */
	public Boolean getEditableByStudent() {
		return editableByStudent;
	}

	/**
	 * @param editableByStudent
	 *            the editableByStudent to set
	 */
	public void setEditableByStudent(Boolean editableByStudent) {
		this.editableByStudent = editableByStudent;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the transitionSubmissionStates
	 */
	public Set<SubmissionState> getTransitionSubmissionStates() {
		return transitionSubmissionStates;
	}

	/**
	 * @param transitionSubmissionStates
	 *            the transitionSubmissionStates to set
	 */
	public void setTransitionSubmissionStates(Set<SubmissionState> transitionSubmissionStates) {
		this.transitionSubmissionStates = transitionSubmissionStates;
	}
	
	/**
	 * 
	 * @param transitionSubmissionState
	 */
	public void addTransitionSubmissionState(SubmissionState transitionSubmissionState) {
		getTransitionSubmissionStates().add(transitionSubmissionState);
	}
	
	/**
	 * 
	 * @param transitionSubmissionState
	 */
	public void removeTransitionSubmissionState(SubmissionState transitionSubmissionState) {
		getTransitionSubmissionStates().remove(transitionSubmissionState);
	}
	
}
