package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.EmailTemplate;

public interface EmailTemplateRepoCustom {

    public EmailTemplate create(String name, String subject, String message);
    
    public EmailTemplate findByNameOverride(String name);

    public void reorder(Integer src, Integer dest);
    
    public void sort(String column);
    
    public void remove(Integer index);
}
