package org.tdl.vireo.model.export.enums;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;

public enum DSpaceMETSKey {
    AGENT,
    STUDENT_SHORT_NAME,
    STUDENT_FULL_NAME_WITH_BIRTH_YEAR,
    SUBMISSION_TYPE,
    PRIMARY_DOCUMENT_MIMETYPE,
    PRIMARY_DOCUMENT_FIELD_VALUE,
    SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES,
    LICENSE_DOCUMENT_FIELD_VALUES,
    METS_FIELD_VALUES;

    public Object getValue(DSpaceMETSKey key, Submission submission) {
        switch (key) {
            case AGENT:
                return "Vireo DSpace METS packager";
            case LICENSE_DOCUMENT_FIELD_VALUES:
                return submission.getLicenseDocumentFieldValues() != null ? submission.getLicenseDocumentFieldValues() : new ArrayList<FieldValue>();
            case PRIMARY_DOCUMENT_FIELD_VALUE:
                return submission.getPrimaryDocumentFieldValue();
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "application/pdf";
                Tika tika = new Tika();
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    Path path = Paths.get(getPath(primaryDocumentFieldValue.getValue()));
                    primaryDocumentType = tika.detect(path.toString());
                }
                return primaryDocumentType;
            case STUDENT_FULL_NAME_WITH_BIRTH_YEAR:
                List<String> info = new ArrayList<String>();
                String result = "";
                String firstName = getSubmitterFirstName(submission);
                String middleName = getSubmitterMiddleName(submission);
                String lastName = getSubmitterLastName(submission);
                String birthYear = getBirthYear(submission);
                return (lastName.length() > 0 ? lastName + "," : "") + (firstName.length() > 0 ? firstName + " " : "") + (middleName.length() > 0 ? middleName + " " : "") + (birthYear.length() > 0 ? birthYear + "-" : "");
            case STUDENT_SHORT_NAME:
                String shortFirstName = getSubmitterFirstName(submission);
                String shortLastName = getSubmitterLastName(submission);
                return (shortFirstName.length() > 0 ? shortFirstName + " " : "") + (shortLastName.length() > 0 ? shortLastName : "");
            case SUBMISSION_TYPE:
                Optional<String> submissionType = getFieldValueByPredicateValue(submission, "submission_type");
                return (submissionType.isPresent() ? submissionType.get() : "");
            case SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES:
                return submission.getSupplementalAndSourceDocumentFieldValues() != null ? submission.getSupplementalAndSourceDocumentFieldValues() : new ArrayList<FieldValue>();
            case METS_FIELD_VALUES:
                return submission.getFieldValues().parallelStream().filter(new Predicate<FieldValue>() {
                    @Override
                    public boolean test(FieldValue fv) {
                        return fv.getFieldPredicate().getSchema().equals("dc") || fv.getFieldPredicate().getSchema().equals("thesis") || fv.getFieldPredicate().getSchema().equals("local");
                    }
                }).collect(Collectors.toList());
            default:
                return "";
        }
    }

    public  String getPath(String relativePath) {
        String path = Application.BASE_PATH + relativePath;
        if (path.contains(":") && path.charAt(0) == '/') {
            path = path.substring(1, path.length());
        }
        return path;
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

    public String getBirthYear(Submission submission) {
        Optional<String> birthYear = getFieldValueByPredicateValue(submission, "birth_year");
        return birthYear.isPresent() ? birthYear.get() : "";
    }

    public Optional<String> getFieldValueByPredicateValue(Submission submission, String predicateValue) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 ? Optional.of(fieldValues.get(0).getValue()) : Optional.empty();
    }
}
