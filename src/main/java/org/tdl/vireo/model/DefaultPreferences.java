package org.tdl.vireo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultPreferences {
    protected ArrayList<String> allowedKeys;
    protected Map<String,String> preferences;
    protected String type;

    public DefaultPreferences(String type, Map<String, String> preferences) {
        this.allowedKeys = new ArrayList<String>();
        setType(type);
        setPreferences(preferences);
        preferences.forEach((k,v) -> {
            this.addAllowedKey(k);
        });
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }
        
    public Map<String, String> getPreferences() {
        return this.preferences;
    }
    
    public String getType() {
        return this.type;
    }
    
    protected void setAllowedKeys(List<String> keys) {
        this.allowedKeys.addAll(keys);
    }

    protected void addAllowedKey(String key) {
        this.allowedKeys.add(key);
    }
    
    public List<String> getAllowedKeys() {
        return this.allowedKeys;
    }
}
