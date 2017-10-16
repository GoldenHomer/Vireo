package org.tdl.vireo.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.validation.model.BusinessValidationType.CREATE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.DELETE;
import static edu.tamu.weaver.validation.model.BusinessValidationType.UPDATE;
import static edu.tamu.weaver.validation.model.MethodValidationType.REORDER;
import static edu.tamu.weaver.validation.model.MethodValidationType.SORT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.repo.LanguageRepo;
import org.tdl.vireo.service.ProquestCodesService;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidation;

/**
 * Controller in which to manage langauges.
 *
 */
@Controller
@ApiMapping("/settings/language")
public class LanguageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ProquestCodesService proquestCodesService;

    /**
     *
     * @return
     */
    @Transactional
    @ApiMapping("/all")
    @Auth(role = "MANAGER")
    public ApiResponse getAllLanguages() {
        return new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc());
    }

    /**
     *
     * @return
     */
    @Auth(role = "MANAGER")
    @ApiMapping(value = "/create", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = CREATE) })
    public ApiResponse createLanguage(@WeaverValidatedModel Language language) {
        logger.info("Creating language with name " + language.getName());
        language = languageRepo.create(language.getName());
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, language);
    }

    /**
     *
     * @return
     */
    @Auth(role = "MANAGER")
    @ApiMapping(value = "/update", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = UPDATE) })
    public ApiResponse updateLanguage(@WeaverValidatedModel Language language) {
        logger.info("Updating language with name " + language.getName());
        language = languageRepo.save(language);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS, language);
    }

    /**
     *
     * @return
     */
    @Auth(role = "MANAGER")
    @ApiMapping(value = "/remove", method = POST)
    @WeaverValidation(business = { @WeaverValidation.Business(value = DELETE, joins = { FieldGloss.class, ControlledVocabulary.class }) })
    public ApiResponse removeLanguage(@WeaverValidatedModel Language language) {
        logger.info("Removing language with name " + language.getName());
        languageRepo.remove(language);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to reorder languages.
     *
     * @param src
     *            source position
     * @param dest
     *            destination position
     * @return ApiResponse indicating success
     */
    @ApiMapping("/reorder/{src}/{dest}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = REORDER, model = Language.class, params = { "0", "1" }) })
    public ApiResponse reorderLanguage(@ApiVariable Long src, @ApiVariable Long dest) {
        logger.info("Reordering languages");
        languageRepo.reorder(src, dest);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to sort languages.
     *
     * @param column
     *            column to sort by
     * @return ApiResponse indicating success
     */
    @ApiMapping("/sort/{column}")
    @Auth(role = "MANAGER")
    @WeaverValidation(method = { @WeaverValidation.Method(value = SORT, model = Language.class, params = { "0" }) })
    public ApiResponse sortLanguage(@ApiVariable String column) {
        logger.info("Sorting languages by " + column);
        languageRepo.sort(column);
        simpMessagingTemplate.convertAndSend("/channel/settings/language", new ApiResponse(SUCCESS, languageRepo.findAllByOrderByPositionAsc()));
        return new ApiResponse(SUCCESS);
    }

    /**
     *
     * @return
     */
    @ApiMapping("/proquest")
    @Auth(role = "MANAGER")
    public ApiResponse getProquestLanguageCodes() {
        return new ApiResponse(SUCCESS, proquestCodesService.getCodes("languages"));
    }

}
