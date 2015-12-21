
package org.tdl.vireo.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.custom.ConfigurationRepoCustom;
import org.tdl.vireo.service.DefaultSettingsService;

public class ConfigurationRepoImpl implements ConfigurationRepoCustom {

    @Autowired
    ConfigurationRepo configurationRepo;
    
    @Autowired
    DefaultSettingsService defaultSettingsService;

    /**
     * Creates or updates existing configuration
     * 
     * @param name
     * @param value
     * @return
     */
    @Override
    public Configuration create(String name, String value) {
        Configuration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            configuration.setValue(value);
            return configurationRepo.save(configuration);
        }
        return configurationRepo.save(new Configuration(name, value));
    }

    @Override
    public String getValue(String name, String fallback) {
        String ret = fallback;
        Configuration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            ret = configuration.getValue();
        }
        return ret;
    }
    
    @Override
    public Integer getValue(String name, Integer fallback) {
        Integer ret = fallback;
        Configuration configuration = configurationRepo.findByName(name);
        if (configuration != null) {
            try {
                return Integer.parseInt(configuration.getValue());
            } catch (NumberFormatException e) {
                // do nothing, ret will use fallback
            }
        }
        return ret;
    }
    
    /**
     * Gets a config value from the DB by name and type.
     * If no value is found, it checks the DefaultSettingsService, which returns the default for that name and type if it exists, null otherwise.
     * 
     * @param name
     * @param type
     * @return String
     */
    @Override
    public String getValueByNameAndType(String name, String type) {
        String overrideValue = configurationRepo.getValueByNameAndType(name,type);
        if (overrideValue != null) {
            return overrideValue;
        }
        return defaultSettingsService.getSetting(name, type);
    }
}
