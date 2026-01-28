package com.openclassrooms.notes.controller;

import com.openclassrooms.notes.entity.Note;
import com.openclassrooms.notes.service.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/patient/{patId}")
    public ResponseEntity<List<Note>> getNotesByPatient(@PathVariable Integer patId) {
        return ResponseEntity.ok(noteService.getNotesByPatId(patId));
    }
}
