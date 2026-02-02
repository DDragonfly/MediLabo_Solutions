package com.openclassrooms.notes.service;

import com.openclassrooms.notes.entity.Note;
import com.openclassrooms.notes.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getNotesByPatId(Integer patId) {
        return noteRepository.findByPatId(patId);
    }

    public Note createNote(Note note) {
        note.setId(null);
        return noteRepository.save(note);
    }

    public Note getNoteById(String id) {
        return noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Note with id " + id + " not found"));
    }

    public Note updateNote(String id, Note updated) {
        Note existing = getNoteById(id);
        existing.setPatId(updated.getPatId());
        existing.setPatient(updated.getPatient());
        existing.setNote(updated.getNote());
        return noteRepository.save(existing);
    }
}
