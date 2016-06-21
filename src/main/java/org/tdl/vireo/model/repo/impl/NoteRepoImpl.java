package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

public class NoteRepoImpl implements NoteRepoCustom {
    
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private NoteRepo noteRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Override
    public Note create(WorkflowStep originatingWorkflowStep, String name, String text) {
        Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text));
        originatingWorkflowStep.addOriginalNote(note);
        workflowStepRepo.save(originatingWorkflowStep);
        return noteRepo.findOne(note.getId());
    }
    
    public void disinheritFromWorkflowStep(Organization requestingOrganization, WorkflowStep workflowStep, Note noteToDisinherit) throws WorkflowStepNonOverrideableException, NoteNonOverrideableException {
        
        if(workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || workflowStep.getOverrideable()) {
            
            if(noteToDisinherit.getOriginatingWorkflowStep().getId().equals(noteToDisinherit.getId()) || noteToDisinherit.getOverrideable()) {
            
                // if requesting organization is not the workflow step's orignating organization                        
                if(!workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                    // create a new workflow step
                    workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
                    
                    workflowStep.removeAggregateNote(noteToDisinherit);
                    
                    workflowStepRepo.save(workflowStep);
                }
                else {
                    
                    List<WorkflowStep> workflowStepsContainingNote = getContainingDescendantWorkflowStep(requestingOrganization, noteToDisinherit);
                    
                    if(workflowStepsContainingNote.size() > 0) {
                        
                        boolean foundNewOriginalOwner = false;
                        
                        for(WorkflowStep workflowStepContainingNote : workflowStepsContainingNote) {
                            // add field profile as original to first workflow step
                            if(!foundNewOriginalOwner) {
                                workflowStepContainingNote.addOriginalNote(noteToDisinherit);
                                foundNewOriginalOwner = true;
                            }
                            else {
                                workflowStepContainingNote.addAggregateNote(noteToDisinherit);
                            }
                            workflowStepRepo.save(workflowStepContainingNote);
                        }
                        
                        workflowStep.removeOriginalNote(noteToDisinherit);
                        
                        workflowStepRepo.save(workflowStep);
                        
                    }
                    else {            
                        noteRepo.delete(noteToDisinherit);
                    }
                }
            }
            else {
                throw new NoteNonOverrideableException();
            }
        }
        else {
            throw new WorkflowStepNonOverrideableException();
        }
        
    }
    
    public Note update(Note pendingNote, Organization requestingOrganization) throws NoteNonOverrideableException, WorkflowStepNonOverrideableException {
        
        Note resultingNote = null;
        
        Note persistedNote = noteRepo.findOne(pendingNote.getId());
        
        boolean overridabilityOfPersistedNote = persistedNote.getOverrideable();
        
        boolean overridabilityOfOriginatingWorkflowStep = persistedNote.getOriginatingWorkflowStep().getOverrideable();
        
        
        WorkflowStep workflowStepWithNoteOnRequestingOrganization = null;
        
        boolean requestingOrganizationOriginatedWorkflowStep = false;
        
        boolean workflowStepWithNoteOnRequestingOrganizationOriginatedNote = false;
        
        for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
            if(workflowStep.getAggregateNotes().contains(persistedNote)) {
                workflowStepWithNoteOnRequestingOrganization = workflowStep;
                requestingOrganizationOriginatedWorkflowStep = workflowStepWithNoteOnRequestingOrganization.getOriginatingOrganization().getId().equals(requestingOrganization.getId());
            }
        }
        
        if(workflowStepWithNoteOnRequestingOrganization != null) {
            workflowStepWithNoteOnRequestingOrganizationOriginatedNote = persistedNote.getOriginatingWorkflowStep().getId().equals(workflowStepWithNoteOnRequestingOrganization.getId());
        }
        
        if(!overridabilityOfOriginatingWorkflowStep && !requestingOrganizationOriginatedWorkflowStep) {
            throw new WorkflowStepNonOverrideableException();
        }
        
        if(!overridabilityOfPersistedNote && !(workflowStepWithNoteOnRequestingOrganizationOriginatedNote && requestingOrganizationOriginatedWorkflowStep)) {
            throw new NoteNonOverrideableException();
        }
        
        if(workflowStepWithNoteOnRequestingOrganizationOriginatedNote && requestingOrganizationOriginatedWorkflowStep) {
            resultingNote = noteRepo.save(pendingNote);
        }
        else {
            
            em.detach(pendingNote);
            pendingNote.setId(null);

            WorkflowStep persistedOriginatingWorkflowStep = persistedNote.getOriginatingWorkflowStep();
            
            if(!requestingOrganizationOriginatedWorkflowStep) {
                
                WorkflowStep existingOriginatingWorkflowStep = workflowStepRepo.findByNameAndOriginatingOrganization(persistedOriginatingWorkflowStep.getName(), requestingOrganization);
                
                if(existingOriginatingWorkflowStep == null) {
                    WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(persistedOriginatingWorkflowStep, requestingOrganization);
                  
                    pendingNote.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
                }
                else {
                    pendingNote.setOriginatingWorkflowStep(existingOriginatingWorkflowStep);
                }
                
            }
          
          
            pendingNote.setOriginatingNote(null);
                      
            Note newNote = noteRepo.save(pendingNote);
          
          
            for(WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedNote)) {
                workflowStep.replaceAggregateNote(persistedNote, newNote);
                workflowStepRepo.save(workflowStep);
            }
          
          
            for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
                if(workflowStep.getAggregateNotes().contains(persistedNote)) {
                    workflowStep.replaceAggregateNote(persistedNote, newNote);
                    workflowStepRepo.save(workflowStep);
                }
            }
          
          
            // if parent organization's workflow step updates a field profile originating form a descendent, the original field profile need to be deleted
            if(workflowStepRepo.findByAggregateNotesId(persistedNote.getId()).size() == 0) {
                noteRepo.delete(persistedNote);
            }
            else {
                newNote.setOriginatingNote(persistedNote);
                newNote = noteRepo.save(newNote);
            }
            
            
            
            // TODO: if changed from overrideable to non overrideable, re-inherit
            
            
            
            
            
            
            
            resultingNote = newNote;
            
        }
 
        return resultingNote;
    }

    @Override
    public void delete(Note note) {
        
        // allows for delete by iterating through findAll, while still deleting descendents
        if(noteRepo.findOne(note.getId()) != null) {
        
            WorkflowStep originatingWorkflowStep = note.getOriginatingWorkflowStep();
            
            originatingWorkflowStep.removeOriginalNote(note);
            
            if(note.getOriginatingNote() != null) {
                note.setOriginatingNote(null);
            }
            
            workflowStepRepo.findByAggregateNotesId(note.getId()).forEach(workflowStep -> {
                workflowStep.removeAggregateNote(note);
                workflowStepRepo.save(workflowStep);
            });
            
            noteRepo.findByOriginatingNote(note).forEach(fp -> {
                fp.setOriginatingNote(null);
            });
            
            deleteDescendantsOfNote(note);
            
            noteRepo.delete(note.getId());
        
        }
    }
    
    private void deleteDescendantsOfNote(Note note) {
        noteRepo.findByOriginatingNote(note).forEach(desendantNote -> {
            delete(desendantNote);
        });
    }
    
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, Note note) {
        List<WorkflowStep> descendantWorkflowStepsContainingNote = new ArrayList<WorkflowStep>();
        organization.getAggregateWorkflowSteps().forEach(ws -> {
            if(ws.getAggregateNotes().contains(note)) {
                descendantWorkflowStepsContainingNote.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
            descendantWorkflowStepsContainingNote.addAll(getContainingDescendantWorkflowStep(descendantOrganization, note));
        });
        return descendantWorkflowStepsContainingNote;
    }

}
