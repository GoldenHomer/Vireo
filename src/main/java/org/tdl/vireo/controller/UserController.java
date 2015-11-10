package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired 
    private SimpMessagingTemplate simpMessagingTemplate;
    
    private static final Logger logger = Logger.getLogger(UserController.class);
        
    @ApiMapping("/credentials")
    @Auth
    public ApiResponse credentials(@Shib Object credentials) {
        
        Credentials shib = (Credentials) credentials;
        
        User user = userRepo.findByEmail(shib.getEmail());
        
        if(user == null) {
            logger.debug("User not registered!");
            return new ApiResponse(ERROR, "User not registered!");
        }

        shib.setRole(user.getRole());
        
        return new ApiResponse(SUCCESS, shib);
    }
    
    @ApiMapping("/all")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse allUsers() {            
        Map<String,List<User>> map = new HashMap<String,List<User>>();        
        map.put("list", userRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }
    
    @ApiMapping("/update-role")
    @Auth(role="ROLE_MANAGER")
    @Transactional
    public ApiResponse updateRole(@Data String data) throws Exception {
        
        Map<String,String> map = new HashMap<String,String>();      
        try {
            map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }       
        
        User user = userRepo.findByEmail(map.get("email"));
                
        user.setRole(map.get("role"));
        
        user = userRepo.save(user);
        
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("list", userRepo.findAll());
        userMap.put("changedUserEmail", map.get("email"));
        
        this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        
        return new ApiResponse(SUCCESS, user);
    }
    
}
