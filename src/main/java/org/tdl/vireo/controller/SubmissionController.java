package org.tdl.vireo.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

@Controller
@ApiMapping("/submission")
public class SubmissionController {

    @Autowired
    private SubmissionRepo submissionRepo;
    
    @Autowired
    private FieldValueRepo fieldValueRepo;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")    
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, submissionRepo.findAll());
    }
    
    @ApiMapping("/all-by-user")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse getAllByUser(@ApiCredentials Credentials credentials) {
        
        User submitter = userRepo.findByEmail(credentials.getEmail());
        
        return new ApiResponse(SUCCESS, submissionRepo.findAllBySubmitter(submitter));
    }

    @Transactional
    @ApiMapping("/get-one/{submissionId}")
    @Auth(role = "STUDENT")
    public ApiResponse getOne(@ApiVariable Long submissionId) {
        return new ApiResponse(SUCCESS, submissionRepo.findOne(submissionId));
    }

    @Transactional
    @ApiMapping("/create")
    @Auth(role = "STUDENT")
    public ApiResponse createSubmission(@ApiCredentials Credentials credentials, @ApiData JsonNode dataNode) {
        Submission submission = submissionRepo.create(credentials, dataNode.get("organizationId").asLong());
        simpMessagingTemplate.convertAndSend("/channel/submission", new ApiResponse(SUCCESS, submissionRepo.findAll()));
        return new ApiResponse(SUCCESS, submission);
    }
    
    @ApiMapping("/{submissionId}/update-field-value")
    @Auth(role = "STUDENT")
    @Transactional
    public ApiResponse updateSubmission(@ApiVariable("submissionId") Long submissionId, @ApiModel FieldValue fieldValue) {
                
        Submission submission = submissionRepo.findOne(submissionId);
        
        if(fieldValue.getId() == null) {
            submission.addFieldValue(fieldValue);
            submission = submissionRepo.save(submission);
            fieldValue.getPredicate();
            fieldValue = submission.getFieldValueByValueAndPredicate(fieldValue.getValue(), fieldValue.getPredicate());
        } else {
            fieldValue = fieldValueRepo.save(fieldValue);
            submission = submissionRepo.findOne(submissionId);
        }   
               
        return new ApiResponse(SUCCESS, fieldValue);
    }

}
