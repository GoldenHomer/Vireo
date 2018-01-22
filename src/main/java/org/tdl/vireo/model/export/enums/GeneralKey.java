package org.tdl.vireo.model.export.enums;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.service.DefaultSettingsService;

import edu.tamu.weaver.context.SpringContext;

public enum GeneralKey {
    TIME,
    SUBMISSION_ID,
    FIELD_VALUES,
    ADVISOR_APPROVAL_DATE,
    COMMITTEE_APPROVAL_DATE,
    EMBARGO_APPROVAL_DATE,
    LICENSE_AGREEMENT_DATE,
    SUBMISSION_DATE,
    FORMATTED_APPROVAL_DATE,
    FORMATTED_COMMITTEE_APPROVAL_DATE,
    FORMATTED_EMBARGO_APPROVAL_DATE,
    FORMATTED_LICENSE_AGREEMENT_DATE,
    FORMATTED_SUBMISSION_DATE,
    APPLICATION_GRANTOR,
    EXPORT_RELEASE_STUDENT_CONTACT_INFORMATION,
    PROQUEST_INDEXING,
    PROQUEST_EXTERNAL_ID,
    PROQUEST_INSTITUTION_CODE,
    PROQUEST_APPLY_FOR_COPYRIGHT,
    PROQUEST_SALE_RESTRICTION_CODE,
    PROQUEST_SALE_RESTRICTION_REMOVE,
    PROQUEST_FORMAT_RESTRICTION_CODE,
    PROQUEST_FORMAT_RESTRICTION_REMOVE;

    public String getValue(GeneralKey key, Submission submission) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        switch (key) {
            case TIME:
                return timeFormat.format(new Date());
            case SUBMISSION_ID:
                return submission.getId().toString();
            case ADVISOR_APPROVAL_DATE:
                return submission.getApproveAdvisorDate() != null ? submission.getApproveAdvisorDate().toString() : "";
            case FORMATTED_APPROVAL_DATE:
                return submission.getApproveAdvisorDate() != null ? dateFormat.format(submission.getApproveAdvisorDate().getTime()) : "";
            case EMBARGO_APPROVAL_DATE:
                return submission.getApproveEmbargoDate().toString();
            case FORMATTED_EMBARGO_APPROVAL_DATE:
                return submission.getApproveEmbargoDate() != null ? dateFormat.format(submission.getApproveEmbargoDate().getTime()) : "";
            case SUBMISSION_DATE:
                return submission.getSubmissionDate().toString();
            case FORMATTED_SUBMISSION_DATE:
                return submission.getSubmissionDate() != null ? dateFormat.format(submission.getSubmissionDate().getTime()) : "";
            case COMMITTEE_APPROVAL_DATE:
                return submission.getApproveApplicationDate() != null ? submission.getApproveApplicationDate().toString() : "";
            case FORMATTED_COMMITTEE_APPROVAL_DATE:
                return submission.getApproveApplicationDate() != null ? dateFormat.format(submission.getApproveApplicationDate().getTime()) : "";
            case LICENSE_AGREEMENT_DATE:
                return submission.getSubmissionDate().toString();
            case FORMATTED_LICENSE_AGREEMENT_DATE:
                return submission.getSubmissionDate() != null ? dateFormat.format(submission.getSubmissionDate().getTime()) : "";
            case APPLICATION_GRANTOR:
                return getSettingByNameAndType("grantor", "application") != null ? getSettingByNameAndType("grantor", "application").getValue() : "";
            case EXPORT_RELEASE_STUDENT_CONTACT_INFORMATION:
                String grantor = getSettingByNameAndType("release_student_contact_information", "export").getValue();
                return grantor != null ? grantor : "false";
            case PROQUEST_INDEXING:
                return Boolean.valueOf(getSettingByNameAndType("proquest_indexing", "proquest_umi_degree_code").getValue()) ? "Y" : "N";
            case PROQUEST_APPLY_FOR_COPYRIGHT:
                return Boolean.valueOf(getSettingByNameAndType("apply_for_copyright", "proquest_umi_degree_code").getValue()) ? "yes" : "no";
            case PROQUEST_EXTERNAL_ID:
                Long id = submission.getId();
                String lastName = getSubmitterLastName(submission);
                String externalIdPrefix = getSettingByNameAndType("external_id_prefix", "proquest_umi_degree_code").getValue();
                String institutionCode = getSettingByNameAndType("proquest_institution_code", "proquest_umi_degree_code").getValue();
                return String.join("", institutionCode, externalIdPrefix, String.valueOf(id), lastName);
            case PROQUEST_FORMAT_RESTRICTION_CODE:
                return getSettingByNameAndType("format_restriction_code", "proquest_umi_degree_code").getValue();
            case PROQUEST_FORMAT_RESTRICTION_REMOVE:
                return getSettingByNameAndType("format_restriction_remove", "proquest_umi_degree_code").getValue();
            case PROQUEST_INSTITUTION_CODE:
                return getSettingByNameAndType("proquest_institution_code", "proquest_umi_degree_code").getValue();
            case PROQUEST_SALE_RESTRICTION_CODE:
                return getSettingByNameAndType("sale_restriction_code", "proquest_umi_degree_code").getValue();
            case PROQUEST_SALE_RESTRICTION_REMOVE:
                return getSettingByNameAndType("sale_restriction_remove", "proquest_umi_degree_code").getValue();
            default:
                return "";
        }
    }

    public DefaultConfiguration getSettingByNameAndType(String name, String type) {
        DefaultSettingsService defaultSettingsService = SpringContext.bean(DefaultSettingsService.class);
        return defaultSettingsService.getSettingByNameAndType(name, type);
    }

    public String getSubmitterFirstName(Submission submission) {
        Optional<String> firstName = getFieldValueByPredicateValue(submission, "first_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    public String getSubmitterMiddleName(Submission submission) {
        Optional<String> middleName = getFieldValueByPredicateValue(submission, "middle_name");
        return middleName.isPresent() ? middleName.get() : "";
    }

    public String getSubmitterLastName(Submission submission) {
        Optional<String> lastName = getFieldValueByPredicateValue(submission, "last_name");
        return lastName.isPresent() ? lastName.get() : "";
    }

    public Optional<String> getFieldValueByPredicateValue(Submission submission, String predicateValue) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 ? Optional.of(fieldValues.get(0).getValue()) : Optional.empty();
    }
}
