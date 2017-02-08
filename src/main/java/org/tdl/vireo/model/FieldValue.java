package org.tdl.vireo.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.tdl.vireo.model.validation.FieldValueValidator;

import edu.tamu.framework.model.BaseEntity;

@Entity
public class FieldValue extends BaseEntity {

    @Column(columnDefinition = "text", nullable = true)
    private String value;
    
    @Column(nullable = true)
    private String identifier;

    @ManyToOne(cascade = { DETACH, REFRESH, MERGE }, optional = false)
    private FieldPredicate fieldPredicate;

    public FieldValue() {}

    /**
     * 
     * @param predicate
     */
    public FieldValue(SubmissionFieldProfile submissionFieldProfile) {
        this();
        setModelValidator(new FieldValueValidator(submissionFieldProfile));
        setFieldPredicate(submissionFieldProfile.getFieldPredicate());
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
     * @return the predicate
     */
    public FieldPredicate getFieldPredicate() {
        return fieldPredicate;
    }

    /**
     * @param predicate
     *            the predicate to set
     */
    public void setFieldPredicate(FieldPredicate fieldPredicate) {
        this.fieldPredicate = fieldPredicate;
    }
    
}
