package com.openclassrooms.notes.config;
import org.springframework.data.mongodb.MongoDatabaseFactory;


import com.openclassrooms.notes.entity.Note;
import com.openclassrooms.notes.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NoteRepository noteRepository;
    private final MongoDatabaseFactory mongoDatabaseFactory;

    public DataInitializer(NoteRepository noteRepository, MongoDatabaseFactory mongoDatabaseFactory) {
        this.noteRepository = noteRepository;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    @Override
    public void run(String... args) {
        System.out.println("MongoDB database in use: " + mongoDatabaseFactory.getMongoDatabase().getName());

        if (noteRepository.count() > 0) {
            return;
        }

       Note n = new Note();
        n.setPatId(1);
        n.setPatient("TestNone");
        n.setNote("Le patient déclare qu'il se sent très bien");
        noteRepository.save(n);
    }
}
