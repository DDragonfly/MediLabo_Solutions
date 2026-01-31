package com.openclassrooms.notes.controller;

import com.openclassrooms.notes.entity.Note;
import com.openclassrooms.notes.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note created = noteService.createNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
