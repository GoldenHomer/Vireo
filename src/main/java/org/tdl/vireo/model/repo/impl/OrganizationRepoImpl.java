package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

public class OrganizationRepoImpl implements OrganizationRepoCustom {

	@Autowired
	private OrganizationRepo organizationRepo;
	
	@Override
	public Organization create(String name, OrganizationCategory category) {
		return organizationRepo.save(new Organization(name, category));
	}
	
//	@Override
//	public Organization update(Organization organization) {
//		return organizationRepo.update(organization);
//	}
//	
//	@Override
//	public void delete(Organization organization) {
//		organizationRepo.delete(organization);
//	}
	
}
