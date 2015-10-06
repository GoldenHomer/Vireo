package org.tdl.vireo.model.repo.custom;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;

public interface SubmissionRepoCustom {

    // TODO: must be create with arguments state and person
    public Submission create(User submitter, SubmissionState state);

}