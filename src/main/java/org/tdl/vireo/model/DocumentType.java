package org.tdl.vireo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.enums.DegreeLevel;
import org.tdl.vireo.model.DocumentType;

/**
 * 
 * @author gad
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "name", "degreeLevel" } ) } )
public class DocumentType extends BaseOrderedEntity {

	@Column(nullable = false, length=255) 
	private String name;

	@Column(nullable = false)
	private DegreeLevel degreeLevel;
	
	/**
	 * 
	 */
	public DocumentType() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * Create a new JpaDocumentTypeImpl
	 * 
	 * @param name
	 *            The name of the new document type.
	 * @param degreeLevel
	 *            The degreeLevel of the new document type.
	 */
	public DocumentType(String name, DegreeLevel degreeLevel, int order) {
		this.name = name;
		this.degreeLevel = degreeLevel;
		this.order = order;
	}

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the degreeLevel
     */
    public DegreeLevel getDegreeLevel() {
        return degreeLevel;
    }

    /**
     * @param degreeLevel the level to set
     */
    public void setLevel(DegreeLevel degreeLevel) {
        this.degreeLevel = degreeLevel;
    }
}
