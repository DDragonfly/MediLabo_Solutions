package com.openclassrooms.notes.repository;

import com.openclassrooms.notes.entity.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByPatId(Integer patId);
}
