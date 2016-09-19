package org.tdl.vireo.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "name" }) })
public class NamedSearchFilterCriteria extends BaseEntity {
    
    @ManyToOne(optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = User.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean publicFlag;

    @Column(nullable = false)
    private Boolean umiRelease;
    
    @Fetch(FetchMode.SELECT)
    @OneToMany(cascade = {REFRESH, MERGE}, fetch = EAGER, orphanRemoval = true)
    private List<FilterCriterion> filterCriteria;
    
    public NamedSearchFilterCriteria() {
        setPublicFlag(false);
        setUmiRelease(false);
        setFilterCriteria(new ArrayList<FilterCriterion>());
    }
    
    public NamedSearchFilterCriteria(User user, String name) {
        this();
        setUser(user);
        setName(name);
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
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
     * @return the publicFlag
     */
    public Boolean getPublicFlag() {
        return publicFlag;
    }

    /**
     * @param publicFlag the publicFlag to set
     */
    public void setPublicFlag(Boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    /**
     * @return the umiRelease
     */
    public Boolean getUmiRelease() {
        return umiRelease;
    }

    /**
     * @param umiRelease the umiRelease to set
     */
    public void setUmiRelease(Boolean umiRelease) {
        this.umiRelease = umiRelease;
    }

    /**
     * @return the filterCriteria
     */
    public List<FilterCriterion> getFilterCriteria() {
        return filterCriteria;
    }

    /**
     * @param filterCriteria the filterCriteria to set
     */
    public void setFilterCriteria(List<FilterCriterion> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }
    
    public void addFilterCriterion(FilterCriterion filterCriterion) {
        if(!filterCriteria.contains(filterCriterion)) {
            filterCriteria.add(filterCriterion);
        }
    }
    
    public void removeFilterCriterion(FilterCriterion filterCriterion) {
        filterCriteria.remove(filterCriterion);
    }

	public FilterCriterion getFilterCriterion(Long criteriaId) {
		for (FilterCriterion filterCriterion:filterCriteria) {
			System.out.println(filterCriterion.getId()+" = "+criteriaId);
			if (filterCriterion.getId() == criteriaId) {
				System.out.println("we did it as a team");
				return filterCriterion;
			}
		}
		return null;
	}
    
}
