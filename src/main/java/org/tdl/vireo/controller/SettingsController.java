package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.DefaultSettingsService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/settings")
public class SettingsController {
    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    DefaultSettingsService defaultSettingsService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @ApiMapping("/all")
    public ApiResponse getSettings() {   
       //a map of the type names to the full configurations for each type
       Map<String, Map<String,String>> typesToConfigPairs = new HashMap<String, Map<String,String>>();
       List<String> allTypes = defaultSettingsService.getTypes();
       for(String type:allTypes) {
           typesToConfigPairs.put(type,configurationRepo.getAllByType(type));
       }
       return new ApiResponse(SUCCESS,typesToConfigPairs);
    }
    
    @ApiMapping("/update")
    public ApiResponse updateSetting(@Data String data) {
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }       
        configurationRepo.create(map.get("setting"),map.get("value"),map.get("type"));
        
        Map<String, Map<String,String>> typeToConfigPair = new HashMap<String, Map<String,String>>();
        typeToConfigPair.put(map.get("type"),configurationRepo.getAllByType(map.get("type")));
        this.simpMessagingTemplate.convertAndSend("/channel/settings", new ApiResponse(SUCCESS, typeToConfigPair));

        return new ApiResponse(SUCCESS);
    }
    
    @ApiMapping("/reset")
    public ApiResponse resetSetting(@Data String data) {
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        Configuration deletableOverride = configurationRepo.findByNameAndType(map.get("setting"),map.get("type"));
        if (deletableOverride != null) {
            System.out.println(deletableOverride.getName());
            configurationRepo.delete(deletableOverride);
        }
        Map<String, Map<String,String>> typeToConfigPair = new HashMap<String, Map<String,String>>();
        typeToConfigPair.put(map.get("type"),configurationRepo.getAllByType(map.get("type")));
        this.simpMessagingTemplate.convertAndSend("/channel/settings", new ApiResponse(SUCCESS, typeToConfigPair));
        
        return new ApiResponse(SUCCESS);
    }

}
