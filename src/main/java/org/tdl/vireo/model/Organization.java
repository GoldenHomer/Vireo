package org.tdl.vireo.model;

import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Organization extends BaseEntity {
	Set<Organization> parentOrganizations;
	Set<Organization> childrenOrganizations;
	Workflow workflow;
	String name;
	Set<String> emails;
}
